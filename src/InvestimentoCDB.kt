import java.text.NumberFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

class InvestimentoCDB {

    companion object {

        // Função para calcular a alíquota do IR com base nos meses de investimento
        private fun calcularAliquotaIR(mesesInvestimento: Int): Double {
            return when {
                mesesInvestimento <= 180 -> 0.225 // 22,5%
                mesesInvestimento <= 360 -> 0.200 // 20,0%
                mesesInvestimento <= 720 -> 0.175 // 17,5%
                else -> 0.150 // 15,0%
            }
        }

        // Função para calcular e exibir as informações mensais
        private fun mostrarInformacoesMensais(formatador: NumberFormat, taxaCDIMensal: Double, mesesInvestimento: Int, investimentoInicial: Double, investimentoMensal: Double) {
            var valorTotal = investimentoInicial
            var valorTotalAnterior = investimentoInicial

            for (mes in 1..mesesInvestimento) {
                val aliquotaIR = calcularAliquotaIR(mes)
                valorTotal *= (1 + taxaCDIMensal)
                valorTotal += investimentoMensal // Incluindo o aporte mensal

                val ganhoMensalBruto = valorTotal - valorTotalAnterior
                val imposto = ganhoMensalBruto * aliquotaIR
                val ganhoMensalLiquido = ganhoMensalBruto - imposto

                val percentualGanho = if (valorTotalAnterior > 0) ganhoMensalLiquido / valorTotalAnterior * 100 else 0.0

                println("Mês $mes: Valor parcial = ${formatador.format(valorTotal)}, Ganho no mês (bruto) = ${formatador.format(ganhoMensalBruto)}, Ganho no mês (líquido) = ${formatador.format(ganhoMensalLiquido)}, Percentual de ganho líquido = ${String.format("%.2f", percentualGanho)}%")

                valorTotalAnterior = valorTotal
            }
        }


        // Função para gerar e exibir o gráfico do valor acumulado por mês
        private fun mostrarGraficoAcumulado(formatador: NumberFormat, taxaCDIMensal: Double, aliquotaIR: Double, mesesInvestimento: Int, investimentoInicial: Double, investimentoMensal: Double, minAsteriscos: Int, maxAsteriscos: Int) {
            var valorTotal = investimentoInicial
            var valorTotalAnterior = investimentoInicial

            println("\nGráfico do Valor Acumulado por Mês:")
            for (mes in 1..mesesInvestimento) {
                valorTotal *= (1 + taxaCDIMensal)
                valorTotal += investimentoMensal // Incluindo o aporte mensal

                val ganhoMensalBruto = valorTotal - valorTotalAnterior
                val imposto = ganhoMensalBruto * aliquotaIR
                val ganhoLiquidoMes = ganhoMensalBruto - imposto

                val ganhoPercentualAtual = ((valorTotal - investimentoInicial) / investimentoInicial) * 100
                val numAsteriscos = min(maxAsteriscos, max(minAsteriscos, ganhoPercentualAtual.toInt()))

                println("Mês $mes: ${formatador.format(valorTotal)} ${"*".repeat(numAsteriscos)} [${String.format("%.2f", ganhoPercentualAtual)}%] Ganho Líquido: ${formatador.format(ganhoLiquidoMes)}")

                valorTotalAnterior = valorTotal
            }
        }

        // Função para monitorar e informar a quantidade de meses para duplicar, triplicar o investimento, etc.
        private fun monitorarMultiplosDoInvestimentoInicial(formatador: NumberFormat, taxaCDIMensal: Double, mesesInvestimento: Int, investimentoInicial: Double, investimentoMensal: Double) {
            var valorTotal = investimentoInicial
            var multiploAtual = 1

            for (mes in 1..mesesInvestimento) {
                valorTotal *= (1 + taxaCDIMensal)
                valorTotal += investimentoMensal // Incluindo o aporte mensal

                if (valorTotal >= investimentoInicial * (multiploAtual + 1)) {
                    multiploAtual++
                    val percentualGanho = ((valorTotal - investimentoInicial) / investimentoInicial) * 100
                    println("No mês $mes: O investimento alcançou ${multiploAtual}x o valor inicial (${formatador.format(valorTotal)}) [${String.format("%.2f", percentualGanho)}%]")
                }
            }
        }

        // Função para exibir resumo do investimento
        private fun exibirResumoInvestimento(formatador: NumberFormat, taxaCDIMensal: Double, mesesInvestimento: Int, investimentoInicial: Double, investimentoMensal: Double, taxaInflacaoAnual: Double = 0.045) {
            var valorTotal = investimentoInicial
            for (mes in 1..mesesInvestimento) {
                valorTotal *= (1 + taxaCDIMensal)
                valorTotal += investimentoMensal
            }

            val totalAportesMensais = investimentoMensal * mesesInvestimento
            val ganhoTotal = valorTotal - (investimentoInicial + totalAportesMensais)
            val percentualGanho = ganhoTotal / investimentoInicial * 100
            val taxaCDIAnual = (Math.pow(1 + taxaCDIMensal, 12.0) - 1) * 100 // Convertendo a taxa CDI mensal para anual

            val inflacaoAcumulada = Math.pow(1 + taxaInflacaoAnual, mesesInvestimento / 12.0)
            val valorFinalAjustadoInflacao = valorTotal / inflacaoAcumulada
            val perdaPorInflacao = valorTotal - valorFinalAjustadoInflacao
            val percentualPerdaInflacao = perdaPorInflacao / ganhoTotal * 100

            println("\nResumo do Investimento: $mesesInvestimento meses - CDI Anual: ${String.format("%.2f", taxaCDIAnual)}%")
            println("Valor Inicial Investido: ${formatador.format(investimentoInicial)}")
            println("Total de Aportes Mensais: ${formatador.format(totalAportesMensais)}")
            println("Valor Final: ${formatador.format(valorTotal)}")
            println("Valor Final Ajustado pela Inflação: ${formatador.format(valorFinalAjustadoInflacao)}")
            println("Ganhos Totais: ${formatador.format(ganhoTotal)} (${String.format("%.2f", percentualGanho)}%)")
            println("Ganhos Reais: ${formatador.format(ganhoTotal - perdaPorInflacao)}")
            println("Perda de Valor pela Inflação: ${formatador.format(perdaPorInflacao)} (${String.format("%.2f", percentualPerdaInflacao)}%)")
        }

        // Método main
        @JvmStatic
        fun main(args: Array<String>) {
            val formatador = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

            val investimentoInicial = 100000.toDouble() // Valor inicial
            val investimentoMensal = 10000.toDouble() // Aporte mensal
            val mesesInvestimento = 12 // Duração do investimento em meses

            val taxaCDIAnualBase = 0.135 // Taxa CDI anual base de 13,5%
            val multiplicadorCDI = 1.00 // Multiplicador da taxa CDI
            val taxaCDIAnual = taxaCDIAnualBase * multiplicadorCDI
            val taxaCDIMensal = Math.pow(1 + taxaCDIAnual, 1.0 / 12) - 1 // Convertendo a taxa CDI anual para mensal

            val taxaInflacaoAnual = 0.045 // Taxa de inflação anual média estimada de 4,5% a.a

            mostrarInformacoesMensais(formatador, taxaCDIMensal, mesesInvestimento, investimentoInicial, investimentoMensal)
            mostrarGraficoAcumulado(formatador, taxaCDIMensal, calcularAliquotaIR(mesesInvestimento), mesesInvestimento, investimentoInicial, investimentoMensal, 1, 100)
            println()
            monitorarMultiplosDoInvestimentoInicial(formatador, taxaCDIMensal, mesesInvestimento, investimentoInicial, investimentoMensal)
            println()
            exibirResumoInvestimento(formatador, taxaCDIMensal, mesesInvestimento, investimentoInicial, investimentoMensal, taxaInflacaoAnual)
        }
    }
}
