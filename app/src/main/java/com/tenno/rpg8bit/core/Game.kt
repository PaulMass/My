package com.tenno.rpg8bit.core

import android.graphics.Canvas
import com.tenno.rpg8bit.data.Data
import com.tenno.rpg8bit.data.MapDef
import com.tenno.rpg8bit.data.Maps
import com.tenno.rpg8bit.model.Dice
import com.tenno.rpg8bit.model.EnemyDef
import com.tenno.rpg8bit.model.Player
import com.tenno.rpg8bit.model.TipoItem
import com.tenno.rpg8bit.render.Render

class Game(val input: Input) {

    companion object {
        const val W = 320
        const val H = 180
        const val HUD_H = 24
        const val VIEW_W = 20
        const val VIEW_H = 9
    }

    enum class State { TITLE, ROLLS, NAME, CLASS_SELECT, EXPLORE, COMBAT, GAME_OVER, VICTORY }

    var state = State.TITLE
    var frame = 0
    val player = Player()
    val renderer = Render(this)

    var mapIndex = 0
    lateinit var map: MapDef
    var tiles = arrayOf<CharArray>()
    var camX = 0
    var camY = 0
    var moveCd = 0
    var chestCount = 0
    var fireUsed = false
    var abates = 0

    val pending = ArrayDeque<String>()

    var combat: Combat? = null

    var classIndex = 0
    var nameChars = CharArray(8) { ' ' }
    var nameLen = 5
    var nameCursor = 0

    init {
        newRun()
    }

    fun newRun() {
        player.classRef = null
        player.nome = "TENNO"
        player.nivel = 1
        player.xp = 0
        player.stats.rollAll()
        player.pv = 10
        player.pvMax = 10
        player.en = 30
        player.enMax = 30
        player.ouro = 0
        player.bonusDanoExtra = 0
        player.inventario.clear()
        player.skills.clear()
        player.buffs.clear()
        abates = 0
        mapIndex = 0
        nameChars = CharArray(8) { ' ' }
        val n = "TENNO"
        for (i in n.indices) nameChars[i] = n[i]
        nameLen = n.length
        nameCursor = 0
        classIndex = 0
        combat = null
        pending.clear()
        loadMap(0)
    }

    fun loadMap(i: Int) {
        mapIndex = i
        map = Maps.lista[i]
        tiles = map.linhas.map { it.toCharArray() }.toTypedArray()
        chestCount = 0
        fireUsed = false
        for (y in tiles.indices) {
            for (x in tiles[y].indices) {
                if (tiles[y][x] == 'P') {
                    player.x = x
                    player.y = y
                    tiles[y][x] = '.'
                }
            }
        }
        updateCamera()
        player.pv = minOf(player.pvMax, player.pv + player.pvMax * 3 / 10)
        player.en = minOf(player.enMax, player.en + player.enMax * 3 / 10)
        pending.add("${map.nome} - ${map.subnome}")
    }

    fun update() {
        frame++
        if (pending.isNotEmpty()) {
            if (input.wasPressed(Input.Act.A) || input.wasPressed(Input.Act.B)) pending.removeFirst()
            return
        }
        when (state) {
            State.TITLE -> {
                if (input.wasPressed(Input.Act.A)) {
                    newRun()
                    state = State.ROLLS
                }
            }
            State.ROLLS -> {
                if (input.wasPressed(Input.Act.A)) state = State.NAME
                if (input.wasPressed(Input.Act.B)) player.stats.rollAll()
            }
            State.NAME -> updateName()
            State.CLASS_SELECT -> updateClassSelect()
            State.EXPLORE -> updateExplore()
            State.COMBAT -> combat?.update()
            State.GAME_OVER, State.VICTORY -> {
                if (input.wasPressed(Input.Act.A)) state = State.TITLE
            }
        }
    }

    fun render(c: Canvas) = renderer.render(c)

    private fun updateName() {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 .-'"
        if (input.wasPressed(Input.Act.LEFT)) nameCursor = (nameCursor + 7) % 8
        if (input.wasPressed(Input.Act.RIGHT)) nameCursor = (nameCursor + 1) % 8
        if (input.wasPressed(Input.Act.UP)) {
            val i = chars.indexOf(nameChars[nameCursor])
            nameChars[nameCursor] = chars[(i + 1) % chars.length]
            if (nameCursor >= nameLen && nameChars[nameCursor] != ' ') nameLen = nameCursor + 1
        }
        if (input.wasPressed(Input.Act.DOWN)) {
            val i = chars.indexOf(nameChars[nameCursor])
            nameChars[nameCursor] = chars[(i + chars.length - 1) % chars.length]
        }
        if (input.wasPressed(Input.Act.A)) {
            var s = String(nameChars).trim()
            if (s.isEmpty()) s = "TENNO"
            player.nome = s.take(8)
            state = State.CLASS_SELECT
        }
        if (input.wasPressed(Input.Act.B)) {
            if (nameLen > 0) {
                nameLen--
                nameChars[nameLen] = ' '
                nameCursor = nameLen
            }
        }
    }

    private fun updateClassSelect() {
        val n = Data.classes.size
        if (input.wasPressed(Input.Act.UP)) classIndex = (classIndex + n - 1) % n
        if (input.wasPressed(Input.Act.DOWN)) classIndex = (classIndex + 1) % n
        if (input.wasPressed(Input.Act.A)) {
            val c = Data.classes[classIndex]
            player.classRef = c
            player.stats.copyFrom(c.stats)
            player.armaNome = c.armaNome
            player.armaDados = c.armaDados
            player.critRange = c.critRange
            player.pvMax = c.pvBase + player.modCon()
            player.pv = player.pvMax
            player.enMax = 30 + player.modInt() * 2
            player.en = player.enMax
            player.ouro = 20
            player.skills.clear()
            val s0 = Data.skillById(c.skillIds[0])
            if (s0 != null) player.skills.add(s0)
            player.inventario.clear()
            player.inventario.add(Data.itemById("pocao_vida"))
            player.inventario.add(Data.itemById("pocao_vida"))
            player.inventario.add(Data.itemById("pocao_energia"))
            player.inventario.add(Data.itemById("antidoto"))
            state = State.EXPLORE
            pending.add("BEM-VINDO AO SISTEMA DE ORIGEM, ${player.nome}!")
            pending.add("Avance pelas escadas ate a TORRE OROKIN e derrote o CAPITAO VAY HEK!")
        }
        if (input.wasPressed(Input.Act.B)) state = State.NAME
    }

    private fun updateExplore() {
        moveCd--
        if (moveCd <= 0) {
            var dx = 0
            var dy = 0
            if (input.isDown(Input.Act.LEFT)) dx = -1
            else if (input.isDown(Input.Act.RIGHT)) dx = 1
            else if (input.isDown(Input.Act.UP)) dy = -1
            else if (input.isDown(Input.Act.DOWN)) dy = 1
            if (dx != 0 || dy != 0) {
                moveCd = 8
                tryMove(dx, dy)
            }
        }
        if (input.wasPressed(Input.Act.B)) {
            val s = player.stats
            pending.add("FOR ${s.forc} DES ${s.des} CON ${s.con} INT ${s.intl} SAB ${s.sab} CAR ${s.car}")
            pending.add("PV ${player.pv}/${player.pvMax} EN ${player.en}/${player.enMax} ITENS ${player.inventario.size}")
        }
    }

    private fun tryMove(dx: Int, dy: Int) {
        val nx = player.x + dx
        val ny = player.y + dy
        if (nx < 0 || ny < 0 || nx >= map.largura || ny >= map.altura) return
        val t = tiles[ny][nx]
        if (t == '#' || t == 'X') return
        player.x = nx
        player.y = ny
        updateCamera()
        handleTile(nx, ny)
    }

    private fun handleTile(x: Int, y: Int) {
        when (tiles[y][x]) {
            'E' -> {
                tiles[y][x] = '.'
                startCombat(randomEnemy(), false)
            }
            'B' -> {
                tiles[y][x] = '.'
                startCombat(Data.inimigoById(map.bossId ?: "vayhek"), true)
            }
            'C' -> {
                tiles[y][x] = '.'
                openChest()
            }
            'S' -> {
                if (mapIndex + 1 < Maps.lista.size) loadMap(mapIndex + 1)
            }
            'F' -> rest()
            '.' -> if (Dice.roll(100) <= map.encontroChance) startCombat(randomEnemy(), false)
        }
    }

    private fun randomEnemy(): EnemyDef {
        val total = map.pesos.sum()
        var r = Dice.roll(total)
        for (i in map.inimigos.indices) {
            r -= map.pesos[i]
            if (r <= 0) return Data.inimigoById(map.inimigos[i])
        }
        return Data.inimigoById(map.inimigos.last())
    }

    private fun startCombat(def: EnemyDef, boss: Boolean) {
        state = State.COMBAT
        combat = Combat(this, def, boss) { vitoria, fugiu ->
            combat = null
            if (!vitoria && !fugiu) {
                state = State.GAME_OVER
            } else if (vitoria && boss && mapIndex == Maps.lista.lastIndex) {
                state = State.VICTORY
            } else {
                state = State.EXPLORE
            }
        }
    }

    fun onEnemyDefeated(def: EnemyDef, boss: Boolean) {
        val p = player
        abates++
        p.addXp(def.xp)
        val ouro = (def.ouroMin..def.ouroMax).random()
        p.ouro += ouro
        val msgs = mutableListOf<String>()
        msgs.add("VITORIA! +${def.xp} XP, +$ouro OURO.")
        if (Dice.roll(100) <= 35) {
            val it = Data.tabelaSaque.random()
            if (it.tipo == TipoItem.MOD) {
                applyMod(it)
                msgs.add("SAQUE: ${it.nome} EQUIPADO! ${it.desc}")
            } else {
                p.inventario.add(it)
                msgs.add("SAQUE: ${it.nome}! ${it.desc}")
            }
        }
        for (nivel in p.checkLevelUp()) {
            p.pvMax += 10 + p.modCon()
            p.enMax += 5
            p.bonusDanoExtra += 1
            p.pv = p.pvMax
            p.en = p.enMax
            var extra = ""
            if (nivel == 3 && p.skills.size == 1) {
                val c = p.classRef
                val s1 = c?.let { Data.skillById(it.skillIds[1]) }
                if (s1 != null) {
                    p.skills.add(s1)
                    extra = " NOVO PODER: ${s1.nome}!"
                }
            }
            msgs.add("SUBIU PARA O NIVEL $nivel! PV MAX ${p.pvMax}.$extra")
        }
        if (boss) msgs.add("${def.nome} FOI DERROTADO!")
        pending.addAll(msgs)
    }

    private fun applyMod(it: com.tenno.rpg8bit.model.ItemDef) {
        if (it.bonusDano > 0) player.bonusDanoExtra += it.bonusDano
        if (it.bonusPV > 0) {
            player.pvMax += it.bonusPV
            player.pv += it.bonusPV
        }
        if (it.bonusEN > 0) {
            player.enMax += it.bonusEN
            player.en += it.bonusEN
        }
    }

    private fun openChest() {
        chestCount++
        val ouro = if (chestCount == 1) map.bauOuro else map.bauOuro / 2
        player.ouro += ouro
        val msgs = mutableListOf<String>()
        msgs.add("BAU ABERTO! +$ouro OURO.")
        if (chestCount == 1) {
            for (id in map.bauItens) {
                val it = Data.itemById(id)
                if (it.tipo == TipoItem.MOD) {
                    applyMod(it)
                    msgs.add("${it.nome} EQUIPADO! ${it.desc}")
                } else {
                    player.inventario.add(it)
                    msgs.add("ITEM: ${it.nome} - ${it.desc}")
                }
            }
        } else if (Dice.roll(100) <= 60) {
            val it = Data.tabelaSaque.random()
            if (it.tipo == TipoItem.MOD) {
                applyMod(it)
                msgs.add("SAQUE: ${it.nome} EQUIPADO! ${it.desc}")
            } else {
                player.inventario.add(it)
                msgs.add("SAQUE: ${it.nome}! ${it.desc}")
            }
        }
        pending.addAll(msgs)
    }

    private fun rest() {
        if (!fireUsed) {
            fireUsed = true
            player.pv = player.pvMax
            player.en = player.enMax
            pending.add("FOGUEIRA: descanso longo. PV e EN totalmente recuperados!")
        } else {
            player.pv = minOf(player.pvMax, player.pv + player.pvMax / 2)
            player.en = minOf(player.enMax, player.en + player.enMax / 2)
            pending.add("FOGUEIRA: descanso curto. +50% PV e EN.")
        }
    }

    private fun updateCamera() {
        camX = (player.x - VIEW_W / 2).coerceIn(0, map.largura - VIEW_W)
        camY = (player.y - VIEW_H / 2).coerceIn(0, map.altura - VIEW_H)
    }
}
