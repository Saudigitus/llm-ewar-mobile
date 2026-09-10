package org.saudigitus.climasaude.domain.guidance

import org.saudigitus.climasaude.domain.model.TriageGuidance

class TriageRecommendationEngine {
    fun guidance(
        fever: Boolean, dangerSigns: Boolean, malariaTest: Boolean,
        referred: Boolean, alerts: List<String>, alertIds: List<String>
    ): TriageGuidance {
        val steps = when {
            dangerSigns -> listOf(
                if (referred) "Confirme que a criança chegou à unidade de saúde." else "Encaminhe a criança agora para a unidade de saúde, conforme o protocolo APE.",
                "Registe os sinais observados e informe a família sobre a urgência.",
                "Acompanhe a família até confirmar o atendimento."
            )

            fever && !malariaTest -> listOf(
                "Avalie a febre e os outros sintomas conforme o protocolo APE.",
                "Verifique se deve fazer o teste de malária e registe o resultado.",
                "Oriente a família a procurar ajuda se a criança piorar ou apresentar sinais de perigo."
            )

            fever -> listOf(
                "Registe o resultado do teste de malária no sistema clínico.",
                "Siga o protocolo APE para a próxima ação e combine o acompanhamento.",
                "Oriente a família a procurar ajuda se a febre persistir ou surgirem sinais de perigo."
            )

            else -> listOf(
                "Confirme o estado geral da criança e registe a visita.",
                "Combine com a família a próxima visita de acompanhamento.",
                "Oriente a família a procurar ajuda se surgirem febre ou sinais de perigo."
            )
        }
        val contextual = if (alerts.any { it == "RED" || it == "YELLOW" })
            listOf("Há risco de malária assinalado para esta área e data. Reforce a vigilância de febre conforme o protocolo APE.") else emptyList()
        val title = when {
            dangerSigns -> "Encaminhamento urgente"
            fever -> "Acompanhar a febre"
            else -> "Manter o acompanhamento"
        }
        return TriageGuidance(
            title,
            (steps + contextual).take(5),
            "Orientação local · confirmar com o protocolo APE",
            alertIds
        )
    }
}
