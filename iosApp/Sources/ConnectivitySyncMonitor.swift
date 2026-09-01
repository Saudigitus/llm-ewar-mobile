import Foundation
import Network
import ClimaSaudeShared

final class ConnectivitySyncMonitor {
    private let monitor = NWPathMonitor()
    private let queue = DispatchQueue(label: "org.saudigitus.climasaude.connectivity")

    init() {
        monitor.pathUpdateHandler = { path in
            if path.status == .satisfied {
                IosAppKt.syncWhenConnected()
            }
        }
        monitor.start(queue: queue)
    }

    deinit {
        monitor.cancel()
    }
}
