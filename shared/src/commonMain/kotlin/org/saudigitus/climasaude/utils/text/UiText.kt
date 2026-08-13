package org.saudigitus.climasaude.utils.text

import org.saudigitus.climasaude.domain.error.AppError

object UiText {
    const val appName = "Clima Saúde Community"
    const val username = "Utilizador"
    const val password = "Senha"
    const val continueText = "Entrar"
    const val demo = "Experimentar sem conta"
    const val alerts = "Alertas"
    const val triage = "Triagens"
    const val triageTab = "Triagem"
    const val noAlerts = "Sem alertas para a sua área."
    const val sync = "Atualizar"
    const val offlineHint = "Sem internet, continua a ver os últimos alertas recebidos."
    const val steps = "O que fazer"
    const val urgent = "Atenção"
    const val close = "Voltar"
    const val logout = "Sair"
    const val area = "Área"
    const val malaria = "Malária"
    const val low = "Risco baixo"
    const val medium = "Risco médio"
    const val high = "Risco alto"
    const val period = "Período"
    const val settings = "Perfil"
    const val tagline = "Alertas de malária e triagem de crianças, mesmo sem rede."
    const val greeting = "Olá"
    const val translate = "Traduzir"
    const val portuguese = "Português"
    const val xichangana = "Xichangana"
    const val emakhuwa = "Emakhuwa"
    const val alertDetail = "Alerta"
    const val child = "Criança"
    const val addChild = "Adicionar criança"
    const val newTriage = "Nova triagem"
    const val triageDetail = "Triagem"
    const val triageSaved = "Triagem guardada"

    fun pendingRecords(count: Int): String = "$count por enviar"

    fun error(code: AppError): String = when (code) {
        AppError.INVALID_LOGIN -> "Nome de utilizador ou senha incorretos."
        AppError.NO_AREA -> "A sua conta ainda não tem área. Fale com o supervisor."
        AppError.HTTPS_REQUIRED -> "O endereço do servidor tem de começar por https://."
        AppError.ALERTS_NOT_CONFIGURED -> "Os alertas ainda não estão ligados para esta conta."
        AppError.SIGN_IN_AGAIN -> "A sessão expirou. Entre outra vez."
        AppError.CONNECTION -> "Sem ligação. Tente outra vez."
    }
}
