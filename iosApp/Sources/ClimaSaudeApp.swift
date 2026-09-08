import SwiftUI
import ClimaSaudeShared

@main
struct ClimaSaudeApp: App {
    private let connectivitySyncMonitor: ConnectivitySyncMonitor

    init() {
        let baseUrl = Bundle.main.object(forInfoDictionaryKey: "BASE_URL") as? String ?? ""
        let alertsBaseUrl = Bundle.main.object(forInfoDictionaryKey: "ALERTS_BASE_URL") as? String ?? ""
        IosAppKt.startIosApp(baseUrl: baseUrl, alertsBaseUrl: alertsBaseUrl, credentials: KeychainCredentials(), nudgeScheduler: IosNudgeScheduler())
        connectivitySyncMonitor = ConnectivitySyncMonitor()
    }

    var body: some Scene {
        WindowGroup {
            SharedView()
        }
    }
}

struct SharedView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        IosAppKt.mainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
