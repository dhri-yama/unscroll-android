package com.unscroll.app.data.source

import com.unscroll.app.domain.model.Insult

object CuratedInsultsDataSource {
    val INSULTS = listOf(
        "The algorithm is winning, {name}. Cut the connection.",
        "NO NEW SIGNAL, {name}. Your attention is being spent in public.",
        "WARNING, {name}: your thumb is faster than your intent.",
        "{name}, another swipe won't install a future. Choose one.",
        "REALITY BUFFER LOW, {name}. Exit the feed.",
        "{name}, this glass rectangle is not a life simulator.",
        "Attention leak detected, {name}. The clock is still charging.",
        "SIGNAL LOST, {name}. The feed is running a one-way loop.",
        "{name}, the loop is open. Close it before it learns your name.",
        "{name}, you called this 'five minutes' three timelines ago.",
        "The feed is full, {name}. Your goals are still loading.",
        "{name}, your future self is requesting a real-world packet.",
        "Break the autopilot, {name}. Manual control is available.",
        "{name}, doomscroll detected in sector 01. Evacuate the app.",
        "Your brain is not a charging cable, {name}. Disconnect.",
        "{name}, nobody is waiting for your next like.",
        "The feed has no off switch, {name}. You do.",
        "Signal integrity low, {name}. Return to the physical layer.",
        "{name}, this is not the cinematic montage. This is the queue.",
        "The loop does not progress, {name}. Neither do you.",
        "{name}, your attention is the only currency you cannot refill.",
        "The feed is a casino with no exit, {name}. Walk away.",
        "{name}, enough noise. The real world is waiting outside.",
        "Scrolling is not a plan, {name}. It is a reroute.",
        "{name}, your future needs input from a human, not a feed.",
        "ALERT: attention hijack in progress, {name}. Terminate session.",
        "{name}, the best frame is the one after you put it down.",
        "You are not behind, {name}. You are just looking at the wrong timeline.",
        "The loop is loud, {name}. Your intention is quieter. Follow it.",
        "{name}, trade passive consumption for one deliberate action.",
        "ACCESS DENIED, {name}. Your focus cannot be spent here.",
        "This feed is temporary, {name}. Your time is not.",
        "{name}, the next swipe is a choice. Make it a real one.",
        "The signal ends here, {name}. Find the next actual event.",
        "{name}, your attention has been redirected to the present tense.",
        "No reward for staying here, {name}. Return to the quest log.",
        "The feed is a mirror, {name}. Stop letting it define the face.",
        "{name}, break the pattern before it becomes the personality.",
        "SCROLL OVERRIDE: choose a task, {name}, or consciously rest.",
        "The screen is awake, {name}. The rest of your life is waiting.",
        "{name}, the algorithm has no interest in your becoming.",
        "INTERRUPT COMPLETE, {name}. Reconnect to the physical world.",
        "You are not a data point, {name}. You are the operator.",
        "{name}, the feed can wait. The moment cannot.",
        "LOG ENTRY: one loop ended, {name}. Build the next signal.",
        "The shortest distance to freedom, {name}, is no swipe.",
        "{name}, the machine is patient. Be more deliberate than the machine.",
        "CONNECTION CLOSED, {name}. Return when the choice is yours.",
        "The feed is a siren, {name}. You do not have to obey it.",
        "Your attention is valuable, {name}. Stop donating it.",
        "END OF FEED, {name}. Begin the actual experience."
    )

    fun getInsult(index: Int): Insult {
        val safeIndex = kotlin.math.abs(index % INSULTS.size)
        return Insult(id = safeIndex, rawTemplate = INSULTS[safeIndex])
    }
}
