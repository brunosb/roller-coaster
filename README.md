# RollerCoaster
## _Programação Paralela e Concorrente UECE_
##### Professor: Marcial Fernández
---
### Introdução

O Problema da Montanha Russa (Roller Coaster) é um exemplo lúdico de um problema muito comum em controle de processos. O problema simula uma montanha russa onde pessoas entram na fila esperando a vez, depois entram no carro, que quando estiver cheio, parte para a viagem até retornar para pegar novos passageiros.

### Proposta do Problema

O problema da Montanha Russa usa apenas três processos: a montanha russa, o processo main(), os passageiros e o(s) carro(s). Para facilitar o entendimento, sugiro usar para nomes das classes: MontanhaRussa(), Passageiro() e Carro().

O sistema não possui um "controlador" (ou a pessoa que controla a movimentação da montanha russa), isto é,  a função MontanhaRussa() apenas cria os carros e os passageiros. Depois disso, os Passageiro() e Carro() se autocontrolarão sozinhos, isto é, os passageiros saberão a hora de esperar na fila, entrar no carro, sair do carro e o carro saberá quando sair, conforme as condições foram atendidas.

Desenvolver um algoritmo concorrente e códigos para a montanha russa, o carro e os passageiros. Desenvolver uma solução para sincronizá-los usando exclusão mútua com espera bloqueada. Pense em escrever o código genérico, prevendo os demais casos....

Atenção: os tempos indicados não são realistas mas coerentes, para que o tempo de execução do programa seja tolerável (2-3 min).

#### Caso carro único
O primeiro caso é apenas para aquecimento...

Considere a montanha russa com apenas 1 (um) carro com __C__ lugares. Considere __n__ passageiros, que chegam repetidamente e esperam em uma fila na plataforma para entrar no carro, que pode acomodar __C__ passageiros, sendo __C < n__.

O tempo de chegada dos __n__ passageiros à montanha russa é __Tp__, que é aleatório. Atenção: os passageiros deverão ser criados pela função MonhanhaRussa() continuamente atendendo o tempo estabelecido. No entanto, o carro só pode partir e começar o passeio pelo trilho quando estiver cheio (existir o número de pessoas na fila suficiente para enche-lo). Considere um tempo __Te__ como o tempo em que todos os passageiros embarquem e desembarquem do carro. O carro então inicia o passeio que leva um tempo __Tm__ e quando chegar na plataforma, os passageiros saem e entram os novos passageiros.

Considere __n__ = 52, __C__ = 4, __Te__ = 1 seg, __Tm__ = 10 seg, __Tp__ = 1 a 3 seg.

#### Caso 2 carros
Considere agora que existem m carros na montanha russa, sendo m> 1. Uma vez que existe apenas um trilho, os carros não podem passar sobre os outros, isto é, eles devem percorrer o trilho na ordem em que começou. Mais uma vez, um carro só pode sair quando estiver cheio.

Considere __n__ =92 , __m__ = 2, __C__ = 4, __Te__ = 1 seg, __Tm__ = 10 seg, __Tp__ = 1 a 3 seg.

#### Caso 3 carros
Esse é o ultimo caso, três carros simultâneos.

Considere __n__ = 148, __m__ = 3, __C__ = 4,  __Te__ = 1 seg, __Tm__ = 10 seg, __Tp__ = 1 a 3 seg.
Implementação
O trabalho prático consiste em escrever um programa em C, C++, Java ou Python, usando threads, para simular os carros, passageiros e a montanha russa. 

Ao final da execução  deve ser calculado: tempo mínimo, máximo e médio de espera dos passageiros na fila. Tempo de utilização do(s) carros (tempo movimentando/tempo total)