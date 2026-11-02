package com.tenno.rpg8bit.data

enum class Paleta { GRINEER, CORPUS, INFESTADO, OROKIN }

data class MapDef(
    val id: String,
    val nome: String,
    val subnome: String,
    val paleta: Paleta,
    val linhas: List<String>,
    val inimigos: List<String>,
    val pesos: List<Int>,
    val encontroChance: Int,
    val bauItens: List<String>,
    val bauOuro: Int,
    val bossId: String? = null
) {
    val largura: Int = linhas[0].length
    val altura: Int = linhas.size

    init {
        require(linhas.all { it.length == largura }) { "Mapa $id com linhas de larguras diferentes" }
        require(linhas.first().all { it == '#' } && linhas.last().all { it == '#' }) { "Mapa $id sem borda" }
    }
}

object Maps {

    private fun m(
        id: String, nome: String, subnome: String, paleta: Paleta,
        linhas: List<String>,
        inimigos: List<String>, pesos: List<Int>,
        encontro: Int, bau: List<String>, ouro: Int, bossId: String? = null
    ) = MapDef(id, nome, subnome, paleta, linhas, inimigos, pesos, encontro, bau, ouro, bossId)

    val lista: List<MapDef> = listOf(

        m(
            "galeao", "GALEAO GRINEER", "Setor Fóton - Professores", Paleta.GRINEER,
            listOf(
                "########################",
                "#P...E.....#.....E.....#",
                "#..##......#..##.......#",
                "#..#....E..#....#...C..#",
                "#..#.......#####.......#",
                "#..#.................E.#",
                "#..#####..#####..####..#",
                "#......#...........#...#",
                "#.E....#..E.....E...#..#",
                "#......#...........#...#",
                "#..##..#####..####.....#",
                "#..#..............#....#",
                "#..#..C......E....#..S.#",
                "#..#..............#....#",
                "#....E....F.......#..E.#",
                "########################"
            ),
            listOf("lancer", "runner", "butcher"), listOf(3, 2, 1),
            encontro = 6, bau = listOf("pocao_vida", "antidoto"), ouro = 30
        ),

        m(
            "asteroide", "ASTEROIDE GRINEER", "Cinturão de asteroides - Base mineradora", Paleta.GRINEER,
            listOf(
                "########################",
                "#P......#....E.....E...#",
                "#.......#..........#...#",
                "#.E.....#..##......#...#",
                "#......####..E....#....#",
                "#.............#...#....#",
                "#..E....E.....#...#..C.#",
                "#..#########..#...#....#",
                "#..........#..#...#....#",
                "#.E........#..#####....#",
                "#....#.....#......#....#",
                "#.C..#..E..#..E...#....#",
                "#....#.....#......#..S.#",
                "#..#####...####...#....#",
                "#....E.....F......#..E.#",
                "########################"
            ),
            listOf("butcher", "lancer", "moa"), listOf(3, 2, 1),
            encontro = 8, bau = listOf("mod_vitality"), ouro = 50
        ),

        m(
            "navio", "NAVIO CORPUS", "Nefóide - Nave de carga", Paleta.CORPUS,
            listOf(
                "########################",
                "#P....E.....C.....E....#",
                "#..#................#..#",
                "#..#..####....####..#..#",
                "#..#..#.......#.....#..#",
                "#.....#..E....#..E..#..#",
                "#.E...#.......#.....#..#",
                "#.....#####.#####..#...#",
                "#..#...........#....#..#",
                "#..#..E.....E..#....#..#",
                "#..#...........#..C.#..#",
                "#..####..####..#....#..#",
                "#......#......#..E..S..#",
                "#.E....#..F...#......#.#",
                "#......#......#......#.#",
                "########################"
            ),
            listOf("crewman", "moa", "lancer"), listOf(3, 2, 1),
            encontro = 8, bau = listOf("pocao_energia", "pocao_vida"), ouro = 60
        ),

        m(
            "torre", "TORRE OROKIN", "Teto da torre - Trono do Capitão", Paleta.OROKIN,
            listOf(
                "########################",
                "#......................#",
                "#....#............#....#",
                "#....#...E....E...#....#",
                "#....#............#....#",
                "#..........P...........#",
                "#......F...............#",
                "#......................#",
                "#..####..........####..#",
                "#..#................#..#",
                "#..#......#BB#......#..#",
                "#..#......####......#..#",
                "#..#.......C........#..#",
                "########################"
            ),
            listOf("runner", "crewman"), listOf(1, 1),
            encontro = 10, bau = listOf("mod_flow", "mod_serration"), ouro = 150,
            bossId = "vayhek"
        )
    )
}
