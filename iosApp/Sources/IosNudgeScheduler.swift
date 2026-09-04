import Foundation
import UserNotifications
import ClimaSaudeShared

final class IosNudgeScheduler: NSObject, NudgeScheduler {
    private let shown = UserDefaults.standard

    override init() {
        super.init()
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound]) { _, _ in }
    }

    func schedule(id: String, areaName: String, startsAt: String, message: String) {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        guard let start = formatter.date(from: String(startsAt.prefix(10))) else { return }
        let today = Calendar.current.startOfDay(for: Date())
        guard start >= today, start <= Calendar.current.date(byAdding: .day, value: 10, to: today)!,
              !shown.bool(forKey: "nudge-" + id) else { return }
        let content = UNMutableNotificationContent()
        content.title = "Clima Saúde Community: " + areaName
        content.body = message
        content.sound = .default
        let request = UNNotificationRequest(identifier: id, content: content,
                                            trigger: UNTimeIntervalNotificationTrigger(timeInterval: 1, repeats: false))
        UNUserNotificationCenter.current().add(request)
        shown.set(true, forKey: "nudge-" + id)
    }

    func clear() {
        UNUserNotificationCenter.current().removeAllPendingNotificationRequests()
        for key in shown.dictionaryRepresentation().keys where key.hasPrefix("nudge-") {
            shown.removeObject(forKey: key)
        }
    }
}
