package com.tenno.rpg8bit.render

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.tenno.rpg8bit.core.Combat
import com.tenno.rpg8bit.core.Game
import com.tenno.rpg8bit.core.Input
import com.tenno.rpg8bit.data.Data
import com.tenno.rpg8bit.data.Paleta

class Render(val game: Game) {

    private data class Pal(val chao: Int, val chaoVar: Int, val parede: Int, val topo: Int, val det: Int)

    private val paletas = mapOf(
        Paleta.GRINEER to Pal(0xFF3E3A33, 0xFF33302B, 0xFF5C5348, 0xFF7A6E60, 0xFFB08D57),
        Paleta.CORPUS to Pal(0xFFC9D2DE, 0xFFB7C2D2, 0xFFE8EDF4, 0xFFFFFFFF, 0xFF2196F3),
        Paleta.INFESTADO to Pal(0xFF5C2E3C, 0xFF4E2633, 0xFF7A3B4C, 0xFF96505F, 0xFFB0677A),
        Paleta.OROKIN to Pal(0xFFDCD2BC, 0xFFCFC4AC, 0xFFF2EAD8, 0xFFFFFFFF, 0xFFD4AF37)
    )

    private val paint = Paint()

    private val BG = 0xFF0D1422
    private val PANEL = 0xFF14141F
    private val PANEL2 = 0xFF1E1E2E
    private val GOLD = 0xFFD4AF37
    private val WHITE = 0xFFFFFFFF
    private val GRAY = 0xFF90A4AE
    private val RED = 0xFFE53935
    private val BLUE = 0xFF42A5F5
    private val YELLOW = 0xFFFFEB3B
    private val GREEN = 0xFF66BB6A
    private val LIGHTRED = 0xFFFFCDD2
    private val LIGHTBLUE = 0xFFBBDEFB

    fun render(c: Canvas) {
        when (game.state) {
            Game.State.TITLE -> title(c)
            Game.State.ROLLS -> rolls(c)
            Game.State.NAME -> name(c)
            Game.State.CLASS_SELECT -> classSelect(c)
            Game.State.EXPLORE -> explore(c)
            Game.State.COMBAT -> combat(c)
            Game.State.GAME_OVER -> over(c)
            Game.State.VICTORY -> victory(c)
        }
        if (game.pending.isNotEmpty()) messageBox(c)
        touchButtons(c)
    }

    private fun rect(c: Canvas, x: Int, y: Int, w: Int, h: Int) {
        c.drawRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), paint)
    }

    private fun border(c: Canvas, x: Int, y: Int, w: Int, h: Int) {
        rect(c, x, y, w, 1)
        rect(c, x, y + h - 1, w, 1)
        rect(c, x, y, 1, h)
        rect(c, x + w - 1, y, 1, h)
    }

    private fun bar(c: Canvas, x: Int, y: Int, w: Int, h: Int, frac: Float, color: Int) {
        paint.color = 0xFF000000
        rect(c, x, y, w, h)
        paint.color = color
        val fw = (w * frac.coerceIn(0f, 1f)).toInt()
        if (fw > 0) rect(c, x, y, fw, h)
        paint.color = 0xFF424242
        border(c, x, y, w, h)
    }

    private fun darken(color: Int): Int =
        0xFF000000.toInt() or ((color and 0x00FEFEFE) shr 1)

    private fun playerPal(): Map<Char, Int> {
        val cls = game.player.classRef
        return if (cls != null) Sprites.classPalette(cls.corPrim, cls.corSec, cls.corAcento)
        else Sprites.classPalette(0xFFFFFFFF, 0xFF888888, 0xFFD4AF37)
    }

    private fun title(c: Canvas) {
        c.drawColor(BG)
        paint.color = WHITE
        for (i in 0 until 40) {
            val x = (i * 97 + 13) % Game.W
            val y = (i * 57 + 7) % Game.H
            if (i % 3 == 0) rect(c, x, y, 1, 1)
        }
        Sprites.lotus(c, 146, 14, 2, GOLD, 0xFF8A6D1F)
        Font.drawCentered(c, "WARFRAME", 160, 80, 3, GOLD)
        Font.drawCentered(c, "8-BIT RPG", 160, 110, 2, WHITE)
        Font.drawCentered(c, "RPG DE D20 NO SISTEMA DE ORIGEM", 160, 130, 1, GRAY)
        if (game.frame / 25 % 2 == 0) Font.drawCentered(c, "PRESSIONE A", 160, 148, 1, YELLOW)
        Font.drawCentered(c, "DPAD/SETAS MOVER - A CONFIRMA - B VOLTA", 160, 168, 1, 0xFF607D8B)
    }

    private fun rolls(c: Canvas) {
        c.drawColor(BG)
        Font.drawCentered(c, "ROLE SEUS ATRIBUTOS", 160, 18, 2, GOLD)
        Font.drawCentered(c, "4d6 POR ATRIBUTO, MENOR VALOR DESCARTADO", 160, 42, 1, GRAY)
        val s = game.player.stats
        val names = s.names()
        val vals = s.values()
        val mods = s.mods()
        for (i in 0 until 6) {
            val y = 64 + i * 14
            paint.color = PANEL2
            rect(c, 90, y - 2, 140, 12)
            Font.draw(c, names[i], 100, y, 1, WHITE)
            Font.draw(c, "${vals[i]}", 150, y, 1, WHITE)
            val m = mods[i]
            Font.draw(c, if (m >= 0) "+$m" else "$m", 190, y, 1, if (m >= 0) GREEN else RED)
        }
        if (game.frame / 25 % 2 == 0) Font.drawCentered(c, "A: CONTINUAR   B: REROLAR", 160, 158, 1, YELLOW)
    }

    private fun name(c: Canvas) {
        c.drawColor(BG)
        Font.drawCentered(c, "DIGITE O NOME DO TENNO", 160, 26, 2, GOLD)
        Font.drawCentered(c, "(MAX 8 LETRAS)", 160, 50, 1, GRAY)
        for (i in 0 until 8) {
            val x = 88 + i * 18
            val y = 70
            paint.color = if (i == game.nameCursor && game.frame / 20 % 2 == 0) GOLD else 0xFF424242
            border(c, x, y, 16, 16)
            paint.color = PANEL2
            rect(c, x + 1, y + 1, 14, 14)
            val ch = game.nameChars[i]
            if (ch != ' ') Font.drawCentered(c, ch.toString(), x + 8, y + 4, 1, WHITE)
        }
        Font.drawCentered(c, "ESQ/DIR: POSICAO   CIMA/BAIXO: LETRA", 160, 104, 1, GRAY)
        if (game.frame / 25 % 2 == 0) Font.drawCentered(c, "A: CONFIRMAR   B: APAGAR", 160, 122, 1, YELLOW)
    }

    private fun classSelect(c: Canvas) {
        c.drawColor(BG)
        Font.drawCentered(c, "ESCOLHA SEU WARFRAME", 160, 10, 2, GOLD)
        Data.classes.forEachIndexed { i, cls ->
            val y = 38 + i * 16
            val sel = i == game.classIndex
            if (sel) {
                paint.color = PANEL2
                rect(c, 8, y - 3, 104, 14)
                Font.draw(c, ">", 12, y, 1, GOLD)
            }
            Font.draw(c, cls.nome, 24, y, 1, if (sel) GOLD else GRAY)
        }
        val cls = Data.classes[game.classIndex]
        paint.color = PANEL
        rect(c, 120, 34, 196, 134)
        paint.color = GOLD
        border(c, 120, 34, 196, 134)
        Font.draw(c, cls.nome, 128, 40, 2, GOLD)
        Font.draw(c, cls.titulo, 128, 60, 1, WHITE)
        Font.wrap(cls.desc, 42).forEachIndexed { i, l -> Font.draw(c, l, 128, 72 + i * 9, 1, GRAY) }
        val s = cls.stats
        Font.draw(c, "FOR ${s.forc} DES ${s.des} CON ${s.con}", 128, 94, 1, WHITE)
        Font.draw(c, "INT ${s.intl} SAB ${s.sab} CAR ${s.car}", 128, 104, 1, WHITE)
        Font.draw(c, "PV ${cls.pvBase}  ARMA: ${cls.armaNome} (${cls.armaDados})", 128, 118, 1, WHITE)
        val sk = Data.skillById(cls.skillIds[0])
        if (sk != null) {
            Font.draw(c, "PODER: ${sk.nome} (-${sk.custo} EN)", 128, 132, 1, YELLOW)
            Font.wrap(sk.desc, 42).forEachIndexed { i, l -> Font.draw(c, l, 128, 142 + i * 9, 1, GRAY) }
        }
        if (game.frame / 25 % 2 == 0) Font.drawCentered(c, "CIMA/BAIXO ESCOLHE - A CONFIRMA - B VOLTA", 160, 172, 1, YELLOW)
    }

    private fun explore(c: Canvas) {
        c.drawColor(0xFF000000)
        hud(c)
        val pal = paletas[game.map.paleta]!!
        for (ty in game.camY until minOf(game.camY + Game.VIEW_H, game.map.altura)) {
            for (tx in game.camX until minOf(game.camX + Game.VIEW_W, game.map.largura)) {
                drawTile(c, game.tiles[ty][tx], (tx - game.camX) * 16, Game.HUD_H + (ty - game.camY) * 16, tx, ty, pal)
            }
        }
        val bob = if (game.frame / 12 % 2 == 0) 0 else -1
        Sprites.draw(
            c, Sprites.TENNO, playerPal(),
            (game.player.x - game.camX) * 16,
            Game.HUD_H + (game.player.y - game.camY) * 16 + bob
        )
        Font.draw(c, game.map.nome, 210, 171, 1, 0xFF607D8B)
    }

    private fun hud(c: Canvas) {
        paint.color = 0xFF101018
        rect(c, 0, 0, Game.W, Game.HUD_H)
        paint.color = GOLD
        rect(c, 0, Game.HUD_H - 1, Game.W, 1)
        val p = game.player
        Font.draw(c, "${p.nome} NV${p.nivel}  OURO ${p.ouro}  CA ${p.ca}  XP ${p.xp}/${p.xpProximo()}", 4, 3, 1, WHITE)
        bar(c, 4, 14, 100, 6, p.pv.toFloat() / p.pvMax, RED)
        Font.draw(c, "PV ${p.pv}/${p.pvMax}", 108, 14, 1, LIGHTRED)
        bar(c, 190, 14, 70, 6, p.en.toFloat() / p.enMax, BLUE)
        Font.draw(c, "EN ${p.en}", 264, 14, 1, LIGHTBLUE)
    }

    private fun drawTile(c: Canvas, ch: Char, px: Int, py: Int, tx: Int, ty: Int, pal: Pal) {
        val h = (tx * 73856093) xor (ty * 19349663)
        if (ch == '#') {
            paint.color = pal.parede
            rect(c, px, py, 16, 16)
            paint.color = pal.topo
            rect(c, px, py, 16, 3)
            paint.color = pal.det
            if (h and 7 == 0) rect(c, px + 3, py + 8, 2, 2)
            if (h and 7 == 3) rect(c, px + 11, py + 10, 2, 2)
            paint.color = darken(pal.parede)
            rect(c, px, py + 15, 16, 1)
            rect(c, px + 15, py, 1, 16)
            return
        }
        paint.color = if (h and 3 == 0) pal.chaoVar else pal.chao
        rect(c, px, py, 16, 16)
        paint.color = pal.chaoVar
        if (h and 1 == 0) rect(c, px + (h and 12), py + ((h shr 4) and 12), 2, 2)
        paint.color = darken(pal.chao)
        rect(c, px, py, 16, 1)
        rect(c, px, py, 1, 16)
        when (ch) {
            'C' -> {
                paint.color = 0xFF6D4420
                rect(c, px + 2, py + 5, 12, 4)
                paint.color = 0xFF8D5A2B
                rect(c, px + 2, py + 9, 12, 6)
                paint.color = GOLD
                rect(c, px + 7, py + 8, 2, 4)
            }
            'S' -> {
                paint.color = pal.det
                rect(c, px + 3, py + 11, 10, 2)
                rect(c, px + 4, py + 8, 8, 2)
                rect(c, px + 5, py + 5, 6, 2)
            }
            'F' -> {
                paint.color = 0xFF424242
                rect(c, px + 3, py + 11, 10, 3)
                val f = game.frame / 8 % 3
                paint.color = 0xFFE65100
                rect(c, px + 5, py + 7, 6, 6)
                paint.color = 0xFFFF9800
                rect(c, px + 6, py + 6 - f, 4, 7)
                paint.color = 0xFFFFEB3B
                rect(c, px + 7, py + 9, 2, 3)
            }
            'D' -> {
                paint.color = pal.det
                rect(c, px + 2, py + 1, 12, 15)
                paint.color = 0xFF1A1A22
                rect(c, px + 4, py + 3, 8, 13)
            }
            'X' -> {
                paint.color = 0xFF263238
                rect(c, px + 2, py + 4, 12, 10)
                paint.color = if (game.frame / 30 % 2 == 0) pal.det else 0xFF102027
                rect(c, px + 4, py + 6, 8, 4)
            }
        }
    }

    private fun combat(c: Canvas) {
        val cb = game.combat ?: return
        val p = game.player
        c.drawColor(0xFF0A0A12)
        paint.color = PANEL
        rect(c, 0, 0, Game.W, 92)
        paint.color = GOLD
        rect(c, 0, 91, Game.W, 1)
        val pair = Sprites.enemySprite(cb.def.sprite)
        val sc = if (cb.def.tamanho >= 24) 2 else 3
        val es = cb.def.tamanho * sc
        Sprites.draw(c, pair.first, pair.second, Game.W - es - 10, (92 - es) / 2 + 4, sc)
        Font.draw(c, cb.def.nome + if (cb.boss) " (CHEFE)" else "", 8, 6, 1, 0xFFFFD54F)
        bar(c, 8, 20, 130, 7, cb.enemy.pv.toFloat() / cb.def.pv, RED)
        Font.draw(c, "${cb.enemy.pv}/${cb.def.pv}", 144, 20, 1, WHITE)
        if (cb.enemy.atordoado > 0) Font.draw(c, "ATORDOADO!", 8, 32, 1, YELLOW)
        if (cb.boss && cb.def.rajadaTurnos > 0) Font.draw(c, "RAJADA A CADA ${cb.def.rajadaTurnos} TURNOS", 8, 44, 1, 0xFFFF8A80)

        paint.color = PANEL
        rect(c, 0, 92, 132, 88)
        paint.color = GOLD
        rect(c, 131, 92, 1, 88)
        Sprites.draw(c, Sprites.TENNO, playerPal(), 6, 98, 2)
        Font.draw(c, p.nome, 42, 98, 1, WHITE)
        Font.draw(c, "NV ${p.nivel}", 42, 108, 1, GOLD)
        bar(c, 42, 118, 82, 6, p.pv.toFloat() / p.pvMax, RED)
        Font.draw(c, "PV ${p.pv}/${p.pvMax}", 42, 127, 1, LIGHTRED)
        bar(c, 42, 137, 82, 6, p.en.toFloat() / p.enMax, BLUE)
        Font.draw(c, "EN ${p.en}/${p.enMax}", 42, 146, 1, LIGHTBLUE)
        Font.draw(c, "CA ${p.ca}  ${p.armaNome}", 6, 168, 1, GRAY)

        paint.color = 0xFF0D0D16
        rect(c, 136, 92, 184, 48)
        paint.color = 0xFF2A2A3A
        rect(c, 136, 92, 184, 1)
        rect(c, 136, 139, 184, 1)
        cb.log.forEachIndexed { i, line ->
            val col = if (i == cb.log.size - 1) WHITE else 0xFF9E9E9E
            Font.draw(c, line, 140, 96 + i * 11, 1, col)
        }

        when (cb.fase) {
            Combat.Fase.MENU -> {
                val opts = listOf("ATACAR", "PODER", "ITEM", "FUGIR")
                for (i in opts.indices) {
                    val x = 136 + (i % 2) * 92
                    val y = 144 + (i / 2) * 18
                    if (i == cb.sel) {
                        paint.color = GOLD
                        rect(c, x, y, 88, 16)
                        Font.draw(c, opts[i], x + 10, y + 4, 1, 0xFF0A0A12)
                    } else {
                        paint.color = PANEL2
                        rect(c, x, y, 88, 16)
                        Font.draw(c, opts[i], x + 10, y + 4, 1, WHITE)
                    }
                }
            }
            Combat.Fase.PODER -> {
                paint.color = PANEL2
                rect(c, 136, 144, 184, 34)
                Font.draw(c, "PODERES (EN ${p.en}):", 140, 147, 1, GOLD)
                p.skills.forEachIndexed { i, s ->
                    val col = if (i == cb.subSel) YELLOW else 0xFF9E9E9E
                    Font.draw(c, "> ${s.nome} -${s.custo}EN", 140, 157 + i * 9, 1, col)
                }
            }
            Combat.Fase.ITEM -> {
                paint.color = PANEL2
                rect(c, 136, 144, 184, 34)
                Font.draw(c, "ITENS:", 140, 147, 1, GOLD)
                val grupos = p.inventario.filter { it.usavelEmCombate }.groupBy { it.id }.values.toList()
                val start = (cb.subSel - 1).coerceIn(0, maxOf(0, grupos.size - 3))
                for (k in 0 until minOf(3, grupos.size - start)) {
                    val g = grupos[start + k]
                    val i = start + k
                    val col = if (i == cb.subSel) YELLOW else 0xFF9E9E9E
                    Font.draw(c, "> ${g.first().nome} x${g.size}", 140, 157 + k * 9, 1, col)
                }
            }
        }
        if (cb.pause > 0 && game.frame / 20 % 2 == 0) Font.draw(c, ">", 308, 172, 1, GOLD)
    }

    private fun messageBox(c: Canvas) {
        val msg = game.pending.first()
        val lines = Font.wrap(msg, 50)
        val h = 10 + lines.size * 9 + 6
        val x = 50
        val y = 112
        val w = 210
        paint.color = 0xFF000000
        paint.alpha = 225
        rect(c, x, y, w, h)
        paint.alpha = 255
        paint.color = GOLD
        border(c, x, y, w, h)
        lines.forEachIndexed { i, l -> Font.draw(c, l, x + 6, y + 6 + i * 9, 1, WHITE) }
        if (game.frame / 20 % 2 == 0) Font.draw(c, ">", x + w - 10, y + h - 9, 1, GOLD)
    }

    private fun over(c: Canvas) {
        c.drawColor(0xFF1A0508)
        Font.drawCentered(c, "VOCE CAIU EM COMBATE...", 160, 50, 2, RED)
        val p = game.player
        Font.drawCentered(c, "${p.nome} - NIVEL ${p.nivel}", 160, 90, 1, WHITE)
        Font.drawCentered(c, "ABATES ${game.abates}  XP ${p.xp}  OURO ${p.ouro}", 160, 104, 1, GRAY)
        if (game.frame / 25 % 2 == 0) Font.drawCentered(c, "A: TENTAR NOVAMENTE", 160, 140, 1, YELLOW)
    }

    private fun victory(c: Canvas) {
        c.drawColor(BG)
        paint.color = WHITE
        for (i in 0 until 40) {
            val x = (i * 89 + 5) % Game.W
            val y = (i * 47 + 11) % Game.H
            if (i % 2 == 0) rect(c, x, y, 1, 1)
        }
        Sprites.lotus(c, 148, 16, 2, GOLD, 0xFF8A6D1F)
        Font.drawCentered(c, "VITORIA!", 160, 82, 3, GOLD)
        Font.drawCentered(c, "O SISTEMA DE ORIGEM ESTA SEGURO!", 160, 114, 1, WHITE)
        val p = game.player
        Font.drawCentered(c, "${p.nome} - NIVEL ${p.nivel} - ABATES ${game.abates}", 160, 130, 1, GRAY)
        Font.drawCentered(c, "XP ${p.xp}  OURO ${p.ouro}", 160, 142, 1, GRAY)
        if (game.frame / 25 % 2 == 0) Font.drawCentered(c, "A: JOGAR NOVAMENTE", 160, 162, 1, YELLOW)
    }

    private fun touchButtons(c: Canvas) {
        val input = game.input
        dpadButton(c, 16, 124, Input.Act.UP, 0, input.isDown(Input.Act.UP))
        dpadButton(c, 16, 152, Input.Act.DOWN, 1, input.isDown(Input.Act.DOWN))
        dpadButton(c, 4, 138, Input.Act.LEFT, 2, input.isDown(Input.Act.LEFT))
        dpadButton(c, 28, 138, Input.Act.RIGHT, 3, input.isDown(Input.Act.RIGHT))
        paint.alpha = 170
        paint.color = 0xFF1A1A2E
        rect(c, 16, 138, 16, 16)
        paint.alpha = 255
        circleButton(c, 302, 156, 12, "A", input.isDown(Input.Act.A))
        circleButton(c, 274, 164, 10, "B", input.isDown(Input.Act.B))
    }

    private fun dpadButton(c: Canvas, x: Int, y: Int, act: Input.Act, dir: Int, active: Boolean) {
        paint.alpha = 170
        paint.color = if (active) 0xFF5A4A1F else 0xFF1A1A2E
        rect(c, x, y, 16, 16)
        paint.alpha = 255
        paint.color = if (active) GOLD else 0xFF37474F
        border(c, x, y, 16, 16)
        paint.color = if (active) GOLD else 0xFFB0BEC5
        val p = Path()
        when (dir) {
            0 -> { p.moveTo(x + 8f, y + 3f); p.lineTo(x + 3f, y + 10f); p.lineTo(x + 13f, y + 10f) }
            1 -> { p.moveTo(x + 8f, y + 13f); p.lineTo(x + 3f, y + 6f); p.lineTo(x + 13f, y + 6f) }
            2 -> { p.moveTo(x + 3f, y + 8f); p.lineTo(x + 10f, y + 3f); p.lineTo(x + 10f, y + 13f) }
            3 -> { p.moveTo(x + 13f, y + 8f); p.lineTo(x + 6f, y + 3f); p.lineTo(x + 6f, y + 13f) }
        }
        p.close()
        c.drawPath(p, paint)
    }

    private fun circleButton(c: Canvas, x: Int, y: Int, r: Int, label: String, active: Boolean) {
        paint.alpha = 200
        paint.color = if (active) 0xFF5A4A1F else 0xFF263238
        paint.style = Paint.Style.FILL
        c.drawCircle(x.toFloat(), y.toFloat(), r.toFloat(), paint)
        paint.alpha = 255
        paint.color = if (active) GOLD else 0xFF546E7A
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        c.drawCircle(x.toFloat(), y.toFloat(), r.toFloat(), paint)
        paint.style = Paint.Style.FILL
        Font.drawCentered(c, label, x, y - 4, 1, WHITE)
    }
}
