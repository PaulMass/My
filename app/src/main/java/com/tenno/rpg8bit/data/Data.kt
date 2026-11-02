package com.tenno.rpg8bit.data

import com.tenno.rpg8bit.model.ClassDef
import com.tenno.rpg8bit.model.EnemyDef
import com.tenno.rpg8bit.model.ItemDef
import com.tenno.rpg8bit.model.SkillDef
import com.tenno.rpg8bit.model.SkillTipo
import com.tenno.rpg8bit.model.Efeito
import com.tenno.rpg8bit.model.Stats
import com.tenno.rpg8bit.model.TipoItem

object Data {

    val skills: List<SkillDef> = listOf(
        SkillDef("lamina_radial", "LAMINA RADIAL", 20,
            "Corte em area: 2d6 dano. Inimigo faz RES SAB (CD 12) p/ metade.", SkillTipo.DANO,
            dados = "2d6", resiste = true, area = true),
        SkillDef("corte_cego", "CORTE CEGO", 12,
            "Ataque com a arma, +4 no d20. Dano da arma.", SkillTipo.ATAQUE, bonusAtq = 4),
        SkillDef("puxar", "PUXAR", 15,
            "RES SAB do inimigo (CD 12): falha = atordoado por 1 turno.", SkillTipo.DEBUFF,
            efeito = Efeito.ATORDOADO, turnos = 1),
        SkillDef("polarizar", "POLARIZAR", 25,
            "Ataque magico com INT: 2d8 dano vs CA.", SkillTipo.DANO, dados = "2d8", usaInt = true),
        SkillDef("choque", "CHOQUE", 10,
            "Ataque eletrico com DES: 1d8+DES dano vs CA.", SkillTipo.DANO, dados = "1d8", usaDes = true),
        SkillDef("velocidade", "VELOCIDADE", 15,
            "+4 DES por 3 turnos.", SkillTipo.BUFF, efeito = Efeito.VELOCIDADE, turnos = 3),
        SkillDef("bem_estar", "BEM-ESTAR", 15,
            "Cura 3d8 + CAR de PV.", SkillTipo.CURA, cura = "3d8"),
        SkillDef("vinculo", "VINCULO", 15,
            "Causa 1d6+CAR dano e drena metade como cura.", SkillTipo.DANO,
            dados = "1d6", usaCar = true, drena = true),
        SkillDef("pele_ferro", "PELE DE FERRO", 15,
            "Reduz dano recebido em 3 por 3 turnos.", SkillTipo.BUFF, efeito = Efeito.PELE_FERRO, turnos = 3),
        SkillDef("pisao", "PISAO", 20,
            "Golpe em area: 2d6 dano. RES SAB p/ metade.", SkillTipo.DANO,
            dados = "2d6", resiste = true, area = true)
    )

    fun skillById(id: String): SkillDef? = skills.find { it.id == id }

    val itens: List<ItemDef> = listOf(
        ItemDef("pocao_vida", "POCAO DE VIDA", "Cura 2d4+2 PV.", TipoItem.CURA, curaDados = "2d4+2"),
        ItemDef("pocao_energia", "POCAO DE ENERGIA", "Recupera 20 EN.", TipoItem.ENERGIA, energia = 20),
        ItemDef("antidoto", "ANTIDOTO", "Remove atordoamento.", TipoItem.ANTIDOTO),
        ItemDef("mod_serration", "MOD SERRATION", "+2 de dano permanente.", TipoItem.MOD, bonusDano = 2),
        ItemDef("mod_vitality", "MOD VITALITY", "+6 PV maximo.", TipoItem.MOD, bonusPV = 6),
        ItemDef("mod_flow", "MOD FLOW", "+10 EN maximo.", TipoItem.MOD, bonusEN = 10)
    )

    fun itemById(id: String): ItemDef = itens.first { it.id == id }

    val classes: List<ClassDef> = listOf(
        ClassDef("excalibur", "EXCALIBUR", "O Guerreiro", 
            "Espada Umbra edisciplinada. Alto dano corpo a corpo.",
            Stats(16, 12, 14, 10, 10, 12), pvBase = 14, armaNome = "ESPADA UMBRA", armaDados = "1d8", critRange = 20,
            skillIds = listOf("lamina_radial", "corte_cego"),
            corPrim = 0xFFF2F2F2.toInt(), corSec = 0xFF8A8A8A.toInt(), corAcento = 0xFFD4AF37.toInt()),
        ClassDef("mag", "MAG", "A Maga",
            "Manipula campos magneticos. Atordoa e causa dano massivo.",
            Stats(8, 12, 10, 16, 14, 12), pvBase = 9, armaNome = "BOLTOR", armaDados = "1d6", critRange = 20,
            skillIds = listOf("puxar", "polarizar"),
            corPrim = 0xFF3D5AFE.toInt(), corSec = 0xFF1A237E.toInt(), corAcento = 0xFFFF4081.toInt()),
        ClassDef("volt", "VOLT", "O Veloz",
            "Energia eletrica pura. Alta iniciativa e criticos rapidos.",
            Stats(10, 16, 10, 12, 10, 12), pvBase = 11, armaNome = "KUNAI", armaDados = "1d6", critRange = 19,
            skillIds = listOf("choque", "velocidade"),
            corPrim = 0xFFFFD600.toInt(), corSec = 0xFF9E9E9E.toInt(), corAcento = 0xFFFF6D00.toInt()),
        ClassDef("trinity", "TRINITY", "A Cleriga",
            "Cura e sustenta o Tenno em campo.",
            Stats(10, 10, 12, 12, 14, 16), pvBase = 11, armaNome = "LATO", armaDados = "1d6", critRange = 20,
            skillIds = listOf("bem_estar", "vinculo"),
            corPrim = 0xFFEEEEEE.toInt(), corSec = 0xFFB71C1C.toInt(), corAcento = 0xFFD4AF37.toInt()),
        ClassDef("rhino", "RHINO", "O Tanque",
            "Pele de Ferropesada. Quase inatingivel.",
            Stats(14, 8, 16, 8, 10, 10), pvBase = 16, armaNome = "GALATINA", armaDados = "1d10", critRange = 20,
            skillIds = listOf("pele_ferro", "pisao"),
            corPrim = 0xFF9E9E9E.toInt(), corSec = 0xFF424242.toInt(), corAcento = 0xFFC62828.toInt())
    )

    val inimigos: List<EnemyDef> = listOf(
        EnemyDef("runner", "CORREDOR INFESTADO", 5, 10, 3, "1d4+2", 10, 2, 6, "infestado",
            iniciativa = 4, resSab = 0, resDes = 3, tamanho = 16),
        EnemyDef("crewman", "CREWMAN CORPUS", 6, 11, 3, "1d6+1", 15, 8, 18, "corpus",
            iniciativa = 0, resSab = 1, resDes = 2, tamanho = 16),
        EnemyDef("lancer", "LANCER GRINEER", 8, 12, 4, "1d6+2", 20, 5, 12, "grineer",
            iniciativa = 0, resSab = 2, resDes = 1, tamanho = 16),
        EnemyDef("moa", "MOA CORPUS", 10, 12, 4, "1d6+2", 25, 10, 20, "moa",
            iniciativa = 2, resSab = 2, resDes = 3, tamanho = 16),
        EnemyDef("ancient", "ANCIAO INFESTADO", 14, 12, 4, "1d8", 35, 8, 16, "anciao",
            iniciativa = 0, resSab = 3, resDes = 0, dreno = true, tamanho = 16),
        EnemyDef("butcher", "CARNICEIRO GRINEER", 10, 12, 3, "1d6+2", 40, 10, 22, "carniceiro",
            iniciativa = 0, resSab = 2, resDes = 1, tamanho = 16),
        EnemyDef("jackal", "JACKAL", 40, 14, 5, "2d6+2", 200, 50, 80, "jackal",
            iniciativa = 3, resSab = 3, resDes = 4, rajadaTurnos = 3, rajadaDano = "2d8",
            rajadaAtordoa = true, tamanho = 24),
        EnemyDef("vayhek", "CAPITAO VAY HEK", 45, 15, 5, "2d6+2", 500, 100, 150, "vayhek",
            iniciativa = 4, resSab = 4, resDes = 4, rajadaTurnos = 4, rajadaDano = "2d8",
            rajadaAtordoa = true, tamanho = 24)
    )

    fun inimigoById(id: String): EnemyDef = inimigos.first { it.id == id }

    val tabelaSaque: List<ItemDef> = listOf(
        itemById("pocao_vida"), itemById("pocao_vida"), itemById("pocao_energia"),
        itemById("antidoto"), itemById("mod_serration"), itemById("mod_vitality"), itemById("mod_flow")
    )
}
