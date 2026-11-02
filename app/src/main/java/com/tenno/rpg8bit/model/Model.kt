package com.tenno.rpg8bit.model

import java.util.Random

object Dice {
    private val rnd = Random()

    fun roll(sides: Int): Int = 1 + rnd.nextInt(sides)
    fun d20(): Int = roll(20)

    fun roll(n: Int, sides: Int): Int {
        var total = 0
        repeat(n) { total += roll(sides) }
        return total
    }

    fun roll4d6DropLowest(): Int {
        val rolls = IntArray(4) { roll(6) }
        return rolls.sum() - rolls.minOrNull()!!
    }

    fun parse(spec: String): Int {
        var total = 0
        for (part in spec.split('+')) {
            if (part.isEmpty()) continue
            if (part.contains('d')) {
                val pieces = part.split('d')
                total += roll(pieces[0].toInt(), pieces[1].toInt())
            } else {
                total += part.toInt()
            }
        }
        return total
    }
}

fun modAttr(attr: Int): Int = Math.floorDiv(attr - 10, 2)

class Stats(var forc: Int, var des: Int, var con: Int, var intl: Int, var sab: Int, var car: Int) {

    fun copyFrom(o: Stats) {
        forc = o.forc; des = o.des; con = o.con; intl = o.intl; sab = o.sab; car = o.car
    }

    fun rollAll() {
        forc = Dice.roll4d6DropLowest()
        des = Dice.roll4d6DropLowest()
        con = Dice.roll4d6DropLowest()
        intl = Dice.roll4d6DropLowest()
        sab = Dice.roll4d6DropLowest()
        car = Dice.roll4d6DropLowest()
    }

    fun names(): List<String> = listOf("FOR", "DES", "CON", "INT", "SAB", "CAR")
    fun values(): List<Int> = listOf(forc, des, con, intl, sab, car)
    fun mods(): List<Int> = values().map { modAttr(it) }

    fun modFor(): Int = modAttr(forc)
    fun modDes(): Int = modAttr(des)
    fun modCon(): Int = modAttr(con)
    fun modInt(): Int = modAttr(intl)
    fun modSab(): Int = modAttr(sab)
    fun modCar(): Int = modAttr(car)
}

enum class SkillTipo { ATAQUE, DANO, CURA, BUFF, DEBUFF }

enum class Efeito { ATORDOADO, PELE_FERRO, VELOCIDADE, INVISIVEL }

data class SkillDef(
    val id: String,
    val nome: String,
    val custo: Int,
    val desc: String,
    val tipo: SkillTipo,
    val dados: String = "",
    val cura: String = "",
    val resiste: Boolean = false,
    val area: Boolean = false,
    val bonusAtq: Int = 0,
    val usaInt: Boolean = false,
    val usaDes: Boolean = false,
    val usaCar: Boolean = false,
    val efeito: Efeito? = null,
    val turnos: Int = 0,
    val drena: Boolean = false
)

enum class TipoItem { CURA, ENERGIA, ANTIDOTO, MOD }

data class ItemDef(
    val id: String,
    val nome: String,
    val desc: String,
    val tipo: TipoItem,
    val curaDados: String = "",
    val energia: Int = 0,
    val bonusPV: Int = 0,
    val bonusDano: Int = 0,
    val bonusEN: Int = 0
) {
    val usavelEmCombate: Boolean get() = tipo != TipoItem.MOD
}

data class EnemyDef(
    val id: String,
    val nome: String,
    val pv: Int,
    val ca: Int,
    val bonusAtq: Int,
    val dano: String,
    val xp: Int,
    val ouroMin: Int,
    val ouroMax: Int,
    val sprite: String,
    val iniciativa: Int = 0,
    val resSab: Int = 2,
    val resDes: Int = 2,
    val dreno: Boolean = false,
    val rajadaTurnos: Int = 0,
    val rajadaDano: String = "",
    val rajadaAtordoa: Boolean = false,
    val tamanho: Int = 16
)

data class ClassDef(
    val id: String,
    val nome: String,
    val titulo: String,
    val desc: String,
    val stats: Stats,
    val pvBase: Int,
    val armaNome: String,
    val armaDados: String,
    val critRange: Int,
    val skillIds: List<String>,
    val corPrim: Int,
    val corSec: Int,
    val corAcento: Int
)

class Enemy(val def: EnemyDef) {
    var pv: Int = def.pv
    var atordoado: Int = 0
    var turno: Int = 0
}

class Player {
    var classRef: ClassDef? = null
    var nome: String = "TENNO"
    var nivel: Int = 1
    var xp: Int = 0
    val stats = Stats(10, 10, 10, 10, 10, 10)
    var pv: Int = 10
    var pvMax: Int = 10
    var en: Int = 30
    var enMax: Int = 30
    var ouro: Int = 0
    var armaNome: String = "Arma"
    var armaDados: String = "1d6"
    var critRange: Int = 20
    var bonusDanoExtra: Int = 0
    val inventario = mutableListOf<ItemDef>()
    val skills = mutableListOf<SkillDef>()
    val buffs = mutableMapOf<Efeito, Int>()
    var x: Int = 1
    var y: Int = 1

    val ca: Int
        get() = 11 + stats.modDes() + (if (buffs.containsKey(Efeito.INVISIVEL)) 2 else 0)

    val bonusAtq: Int
        get() = stats.modFor() + 2 + (nivel - 1) / 2

    fun modFor(): Int = stats.modFor()
    fun modDes(): Int = stats.modDes() + (if (buffs.containsKey(Efeito.VELOCIDADE)) 4 else 0)
    fun modInt(): Int = stats.modInt()
    fun modSab(): Int = stats.modSab()
    fun modCar(): Int = stats.modCar()
    fun modCon(): Int = stats.modCon()

    fun xpProximo(): Int = when (nivel) {
        1 -> 100
        2 -> 250
        3 -> 500
        else -> Int.MAX_VALUE
    }

    fun temItem(id: String): Boolean = inventario.any { it.id == id }

    fun addXp(amount: Int) {
        xp += amount
    }

    fun checkLevelUp(): List<Int> {
        val subidos = mutableListOf<Int>()
        while (nivel < 5 && xp >= xpProximo()) {
            nivel++
            subidos.add(nivel)
        }
        return subidos
    }
}
