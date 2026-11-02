package com.tenno.rpg8bit.core

import com.tenno.rpg8bit.model.Dice
import com.tenno.rpg8bit.model.Efeito
import com.tenno.rpg8bit.model.Enemy
import com.tenno.rpg8bit.model.EnemyDef
import com.tenno.rpg8bit.model.ItemDef
import com.tenno.rpg8bit.model.SkillDef
import com.tenno.rpg8bit.model.SkillTipo
import com.tenno.rpg8bit.model.TipoItem

class Combat(
    val game: Game,
    val def: EnemyDef,
    val boss: Boolean,
    val onFinish: (vitoria: Boolean, fugiu: Boolean) -> Unit
) {
    enum class Fase { MENU, PODER, ITEM }

    val enemy = Enemy(def)
    val log = ArrayDeque<String>()
    var sel = 0
    var subSel = 0
    var fase = Fase.MENU
    var pause = 40
    var done = false
    var playerStun = 0

    private val p get() = game.player
    private val input get() = game.input

    init {
        val pi = Dice.d20() + p.modDes()
        val ei = Dice.d20() + def.iniciativa
        addLog("INICIATIVA: VOCE $pi X $ei (${def.nome}).")
        if (ei > pi) {
            addLog("O INIMIGO ATACA PRIMEIRO!")
            enemyTurn()
        } else {
            addLog("VOCE ATACA PRIMEIRO!")
        }
    }

    fun addLog(s: String) {
        log.addLast(s)
        while (log.size > 4) log.removeFirst()
    }

    fun update() {
        if (done) return
        if (pause > 0) {
            pause--
            if (input.wasPressed(Input.Act.A) || input.wasPressed(Input.Act.B)) pause = 0
            return
        }
        when (fase) {
            Fase.MENU -> {
                if (input.wasPressed(Input.Act.UP)) sel = (sel + 3) % 4
                if (input.wasPressed(Input.Act.DOWN)) sel = (sel + 1) % 4
                if (input.wasPressed(Input.Act.LEFT)) sel = (sel + 2) % 4
                if (input.wasPressed(Input.Act.RIGHT)) sel = (sel + 2) % 4
                if (input.wasPressed(Input.Act.A)) {
                    when (sel) {
                        0 -> doAttack(null)
                        1 -> { fase = Fase.PODER; subSel = 0 }
                        2 -> { fase = Fase.ITEM; subSel = 0 }
                        3 -> tryFlee()
                    }
                }
            }
            Fase.PODER -> {
                val n = p.skills.size
                if (input.wasPressed(Input.Act.UP)) subSel = (subSel + n - 1) % n
                if (input.wasPressed(Input.Act.DOWN)) subSel = (subSel + 1) % n
                if (input.wasPressed(Input.Act.A)) useSkill(p.skills[subSel])
                if (input.wasPressed(Input.Act.B)) fase = Fase.MENU
            }
            Fase.ITEM -> {
                val grupos = p.inventario.filter { it.usavelEmCombate }.groupBy { it.id }
                if (grupos.isEmpty()) {
                    addLog("SEM ITENS USEAVEIS!")
                    fase = Fase.MENU
                } else {
                    val n = grupos.size
                    if (subSel >= n) subSel = n - 1
                    if (input.wasPressed(Input.Act.UP)) subSel = (subSel + n - 1) % n
                    if (input.wasPressed(Input.Act.DOWN)) subSel = (subSel + 1) % n
                    if (input.wasPressed(Input.Act.A)) {
                        val entry = grupos.entries.elementAt(subSel)
                        useItem(entry.value.first())
                    }
                    if (input.wasPressed(Input.Act.B)) fase = Fase.MENU
                }
            }
        }
    }

    private fun doAttack(skill: SkillDef?) {
        val bonus = p.bonusAtq + (skill?.bonusAtq ?: 0)
        val roll = Dice.d20()
        val total = roll + bonus
        if (roll == 1) {
            addLog("ERRO CRITICO! (1) Nao acertou.")
        } else if (roll >= p.critRange || total >= enemy.def.ca) {
            var dice = Dice.parse(p.armaDados)
            if (roll >= p.critRange) dice += Dice.parse(p.armaDados)
            val dmg = dice + p.modFor() + p.bonusDanoExtra
            enemy.pv -= dmg
            addLog(if (roll >= p.critRange) "ACERTO CRITICO! $dmg de dano!" else "ACERTOU! $total x CA ${enemy.def.ca}: $dmg dano.")
        } else {
            addLog("ERROU. $total x CA ${enemy.def.ca}.")
        }
        afterPlayerAction()
    }

    private fun useSkill(s: SkillDef) {
        if (p.en < s.custo) {
            addLog("ENERGIA INSUFICIENTE! (${p.en}/${s.custo} EN)")
            return
        }
        p.en -= s.custo
        when (s.tipo) {
            SkillTipo.ATAQUE -> {
                addLog("USA ${s.nome}! (-${s.custo} EN)")
                doAttack(s)
                return
            }
            SkillTipo.DANO -> {
                addLog("USA ${s.nome}! (-${s.custo} EN)")
                if (s.resiste) {
                    val save = Dice.d20() + def.resSab
                    var dmg = Dice.parse(s.dados)
                    if (save >= 12) {
                        dmg /= 2
                        addLog("RES SAB $save >= 12: metade do dano!")
                    } else {
                        addLog("FALHOU NA RES ($save < 12): dano cheio!")
                    }
                    enemy.pv -= dmg
                    addLog("$dmg de dano em ${def.nome}.")
                } else {
                    val mod = when {
                        s.usaInt -> p.modInt()
                        s.usaDes -> p.modDes()
                        s.usaCar -> p.modCar()
                        else -> p.modFor()
                    }
                    val roll = Dice.d20()
                    val total = roll + mod + 2
                    if (roll == 1) {
                        addLog("ERRO CRITICO! (1)")
                    } else if (roll >= p.critRange || total >= enemy.def.ca) {
                        var dice = Dice.parse(s.dados)
                        if (roll >= p.critRange) dice += Dice.parse(s.dados)
                        val dmg = dice + mod
                        enemy.pv -= dmg
                        if (s.drena) {
                            val cura = dmg / 2
                            p.pv = minOf(p.pvMax, p.pv + cura)
                            addLog("ACERTOU! $dmg dano e drenou $cura PV.")
                        } else {
                            addLog("ACERTOU! $dmg de dano.")
                        }
                    } else {
                        addLog("ERROU. $total x CA ${enemy.def.ca}.")
                    }
                }
            }
            SkillTipo.CURA -> {
                val v = Dice.parse(s.cura) + p.modCar()
                p.pv = minOf(p.pvMax, p.pv + v)
                addLog("${s.nome}: curou $v PV. (${p.pv}/${p.pvMax})")
            }
            SkillTipo.BUFF -> {
                p.buffs[s.efeito!!] = s.turnos
                addLog("${s.nome} ATIVADO por ${s.turnos} turnos!")
            }
            SkillTipo.DEBUFF -> {
                val save = Dice.d20() + def.resSab
                if (save < 12) {
                    enemy.atordoado = s.turnos
                    addLog("${def.nome} FALHOU NA RES ($save): ATORDOADO!")
                } else {
                    addLog("${def.nome} resistiu ($save >= 12).")
                }
            }
        }
        afterPlayerAction()
    }

    private fun useItem(item: ItemDef) {
        p.inventario.remove(item)
        when (item.tipo) {
            TipoItem.CURA -> {
                val v = Dice.parse(item.curaDados)
                p.pv = minOf(p.pvMax, p.pv + v)
                addLog("${item.nome}: +$v PV. (${p.pv}/${p.pvMax})")
            }
            TipoItem.ENERGIA -> {
                p.en = minOf(p.enMax, p.en + item.energia)
                addLog("${item.nome}: +${item.energia} EN. (${p.en}/${p.enMax})")
            }
            TipoItem.ANTIDOTO -> {
                if (playerStun > 0) {
                    playerStun = 0
                    addLog("ATORDOAMENTO REMOVIDO!")
                } else {
                    addLog("Nada para curar.")
                }
            }
            TipoItem.MOD -> return
        }
        afterPlayerAction()
    }

    private fun tryFlee() {
        val r = Dice.d20() + p.modDes()
        if (r >= 12) {
            addLog("FUGA BEM-SUCEDIDA! (DES $r >= 12)")
            done = true
            onFinish(false, true)
        } else {
            addLog("FUGA FALHOU! (DES $r < 12)")
            enemyTurn()
            if (!done) startPlayerTurn()
        }
    }

    private fun afterPlayerAction() {
        if (enemy.pv <= 0) {
            enemy.pv = 0
            victory()
            return
        }
        enemyTurn()
        if (!done) {
            startPlayerTurn()
            pause = 45
        }
    }

    private fun victory() {
        addLog("${def.nome} DERROTADO!")
        done = true
        game.onEnemyDefeated(def, boss)
        onFinish(true, false)
    }

    private fun enemyTurn() {
        if (done) return
        enemy.turno++
        if (enemy.atordoado > 0) {
            enemy.atordoado--
            addLog("${def.nome} esta atordoado!")
            return
        }
        if (boss && def.rajadaTurnos > 0 && enemy.turno % def.rajadaTurnos == 0) {
            val r = Dice.d20()
            val total = r + p.modDes()
            var dmg = Dice.parse(def.rajadaDano)
            val metade = total >= 13
            if (metade) dmg /= 2
            p.pv -= dmg
            addLog("RAJADA! RES DES $total vs 13: $dmg dano${if (metade) " (metade)" else ""}.")
            if (def.rajadaAtordoa && r == 1) {
                playerStun = 1
                addLog("VOCE FOI ATORDOADO PELA RAJADA!")
            }
        } else {
            val r1 = Dice.d20()
            var roll = r1
            if (p.buffs.containsKey(Efeito.INVISIVEL)) {
                val r2 = Dice.d20()
                if (r2 < roll) roll = r2
            }
            val total = roll + def.bonusAtq
            if (roll == 1) {
                addLog("${def.nome} ERROU CRITICAMENTE! (1)")
            } else if (roll == 20 || total >= p.ca) {
                var dmg = if (roll == 20) Dice.parse(def.dano) * 2 else Dice.parse(def.dano)
                if (p.buffs.containsKey(Efeito.PELE_FERRO)) dmg = maxOf(1, dmg - 3)
                p.pv -= dmg
                addLog("${def.nome} acertou: $dmg dano. PV ${p.pv}/${p.pvMax}.")
                if (def.dreno && dmg > 0) {
                    enemy.pv = minOf(def.pv, enemy.pv + dmg / 2)
                    addLog("Ele drena sua energia vital!")
                }
            } else {
                addLog("${def.nome} errou. $total x CA ${p.ca}.")
            }
        }
        if (p.pv <= 0) {
            p.pv = 0
            addLog("VOCE CAIU EM COMBATE...")
            done = true
            onFinish(false, false)
        }
    }

    private fun startPlayerTurn() {
        val it = p.buffs.entries.iterator()
        while (it.hasNext()) {
            val e = it.next()
            val v = e.value - 1
            if (v <= 0) it.remove() else e.setValue(v)
        }
        if (playerStun > 0) {
            playerStun--
            addLog("VOCE ESTA ATORDOADO! Perdeu o turno.")
            enemyTurn()
        }
    }
}
