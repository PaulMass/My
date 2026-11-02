# Warframe 8-Bit RPG

Um RPG de 8 bits para **Android**, estilo **Dungeons & Dragons**, ambientado no universo de **Warframe**. Você é um Tenno explorando dungeons do Sistema de Origem em combate por turnos com rolagens de **d20**.

> **Aviso legal:** projeto de fã, sem fins lucrativos e sem vínculo com a Digital Extremes. Warframe é marca registrada da Digital Extremes Ltd. Todo o código e arte são originais (gerados proceduralmente em código, sem assets externos).

## Como compilar e rodar

1. Instale o **Android Studio** (versão recente, com JDK 17 integrado).
2. Abra este diretório como projeto (**File > Open**).
3. Aguarde o Gradle sincronizar (na primeira vez ele baixa o Android Gradle Plugin e o plugin Kotlin — precisa de internet nessa etapa).
4. Se o Android Studio avisar sobre o Gradle Wrapper, aceite gerar/usar o wrapper (ou rode `gradle wrapper --gradle-version 8.7` na raiz com um Gradle 8.7+ instalado).
5. Conecte um celular com **Android 7.0 (API 24)+** com depuração USB ativa, ou crie um emulador, e pressione **Run**.

O app **não usa bibliotecas de terceiros**: só o SDK Android + stdlib Kotlin, renderizado em um `SurfaceView` com buffer de 320x180 pixels escalado com filtro *nearest-neighbor* (visual 8-bit de verdade).

## Controles

| Ação | Toque na tela | Teclado |
|---|---|---|
| Mover / navegar | D-pad virtual (canto inferior esquerdo) | Setas ou WASD |
| Confirmar | Botão **A** (canto inferior direito) | Z, Enter ou Espaço |
| Voltar / apagar / ver status | Botão **B** | X ou Back |

## Como jogar

1. **Título** → pressione A.
2. **Atributos**: role 4d6 por atributo (o menor valor é descartado, como no D&D). `B` rerola.
3. **Nome**: escolha letra por letra (max 8 caracteres).
4. **Warframe**: escolha sua classe.
5. **Exploração**: ande pelo mapa. `E` = encontro, `C` = baú, `F` = fogueira (descanso), `S` = escada para o próximo nível, `B` (na Torre Orokin) = chefe final. Andar também pode disparar encontros aleatórios. `B` mostra seus atributos.

### Combate (estilo D&D 5e simplificado)

- **Iniciativa**: d20 + mod DES dos dois lados.
- **Atacar**: d20 + bônus de ataque vs **CA** do inimigo. **20 natural = crítico** (dobra os dados de dano), **1 natural = erro crítico**.
- **Poder**: gasta **EN** (energia). Ex.: Lâmina Radial causa 2d6 em área com teste de resistência (SAB, CD 12) do inimigo; Puxar pode deixar o inimigo **atordoado**.
- **Item**: poções de vida/energia, antídoto.
- **Fugir**: teste de DES (d20 + mod DES vs CD 12).
- **Vitória**: XP, Ouro e saque (poções ou **Mods** que buffam permanentemente). A cada nível: +PV máx, +EN máx, +dano e, no nível 3, um **novo poder**.
- Derrote o **Capitão Vay Hek** na Torre Orokin para vencer o jogo.

### Classes (Warframes)

| Warframe | Papel | Arma | Poder inicial | Poder nível 3 |
|---|---|---|---|---|
| Excalibur | Guerreiro | Espada Umbra (1d8) | Lâmina Radial (2d6 área) | Corte Cego (+4 no ataque) |
| Mag | Maga | Boltor (1d6) | Puxar (atordoa) | Polarizar (2d8 com INT) |
| Volt | Veloz | Kunai (1d6, crítico 19-20) | Choque (1d8 com DES) | Velocidade (+4 DES) |
| Trinity | Clériga | Lato (1d6) | Bem-Estar (cura 3d8+CAR) | Vínculo (dano + drena cura) |
| Rhino | Tanque | Galatina (1d10) | Pele de Ferro (-3 dano) | Pisão (2d6 área) |

### Inimigos

Corredor/Ancião Infestado, Lancer/Carniceiro Grineer, Crewman/MOA Corpus e os chefes **Jackal** e **Capitão Vay Hek** (com rajadas que exigem teste de DES).

## Estrutura do projeto

```
app/src/main/java/com/tenno/rpg8bit/
├── MainActivity.kt          Activity em tela cheia
├── core/
│   ├── Input.kt             estado dos botões (toque + teclado)
│   ├── GameView.kt          SurfaceView, game loop (~30 FPS), botões virtuais
│   ├── Game.kt              máquina de estados (título, criação, exploração...)
│   └── Combat.kt            combate por turnos (d20, CA, testes, XP)
├── model/Model.kt           dados, atributos, modificadores, Player/Enemy
├── data/Data.kt             poderes, itens, classes, inimigos
├── data/Maps.kt             os 4 mapas em ASCII (tilesets Grineer/Corpus/Orokin)
└── render/
    ├── Font.kt              fonte pixel 3x8 com acentos (PT-BR)
    ├── Sprites.kt           sprites 16x16/24x24 em string + paletas
    └── Render.kt            todas as telas, tiles procedurais, HUD, botões
```

## Licença

Código liberado para uso pessoal e educacional (MIT). Novamente: **Warframe © Digital Extremes** — este é apenas um tribute de fã.
