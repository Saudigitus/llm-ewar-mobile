package org.saudigitus.climasaude.domain.guidance

import org.saudigitus.climasaude.domain.model.AppLanguage

data class GuidanceText(
    val lowRisk: String,
    val risingRisk: String,
    val highRisk: String,
    val keepWatch: String,
    val checkSupplies: String,
    val visitFamilies: String,
    val checkFever: String,
    val referSuspected: String,
    val dangerSigns: String
) {
    companion object {
        fun forLanguage(language: AppLanguage): GuidanceText = when (language) {
            AppLanguage.PORTUGUESE -> GuidanceText(
                "Risco baixo", "Risco a subir", "Risco alto",
                "Continue a observar sinais de febre na comunidade.",
                "Confirme que tem testes e materiais para a próxima visita.",
                "Visite famílias com crianças menores de 5 anos.",
                "Pergunte por febre e siga o protocolo APE para testar e tratar.",
                "Encaminhe casos suspeitos segundo o protocolo local.",
                "Se houver sinais de perigo, encaminhe imediatamente para a unidade de saúde."
            )

            AppLanguage.XICHANGANA -> GuidanceText(
                "Khombo yi le hansi", "Khombo ya kula", "Khombo yi le henhla",
                "Yana mahlweni u languta ku hisa ka miri emugangeni.",
                "Languta loko u ri ni swikumiwa swa ku kambela.",
                "Endzela mindyangu leyi nga ni vana va le hansi ka 5 wa malembenu.",
                "Vutisa hi ku hisa ka miri; landzela ndlela ya APE ya ku kambela ni ku pfuna.",
                "Rhumela lava u ehleketaka leswaku va vabya hi ku landza ndlela ya muganga.",
                "Loko ku ri ni swikombiso swa khombo, rhumela munhu hi ku hatlisa exibedhlele."
            )

            AppLanguage.EMAKHUWA -> GuidanceText(
                "Owoopiha vakani", "Owoopiha onnuwa", "Owoopiha wootepa",
                "Mwiiteke ekhalelo ya otthukula erutthu mmuttettheni.",
                "Muwehe alatthu a ovara ni iretthe sa otthuneya.",
                "Mwaaxekure amusi ari ni anamwane ohiwana myaka 5.",
                "Mwaakohe ya otthukula erutthu; mutthare malakiheryo a APE.",
                "Mwarumeele aretta ni malakiheryo a wawo.",
                "Vakhala etthuko ya woopiha, mwarumeele vapharama osipitale."
            )
        }
    }
}
