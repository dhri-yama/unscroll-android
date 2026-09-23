package com.unscroll.app.data.source

import com.unscroll.app.domain.model.Insult

object CuratedInsultsDataSource {
    val INSULTS = listOf(
        "Catch a quick rest, {name}. Your thumb is working harder than your brain right now.",
        "{name}, another 10 minutes of scrolling won't fix your life goals.",
        "Breaking news, {name}: Scrolling reels doesn't count as a career achievement.",
        "Your future self is screaming at you right now, {name}.",
        "{name}, your focus span is shrinking faster than your ambition.",
        "Are you scrolling for a purpose, {name}, or just running away from reality?",
        "Imagine if you put this much discipline into your dreams, {name}.",
        "{name}, you've watched strangers live their lives for another 10 minutes.",
        "Scrolling won't pay your bills or build your empire, {name}.",
        "Hey {name}, your potential is slowly dissolving into short-form content.",
        "{name}, is this reel really worth another fraction of your youth?",
        "Another video, another minute of your prime years gone forever, {name}.",
        "Put the phone down, {name}. The algorithm is winning.",
        "{name}, you are trading your life's vision for 15-second dopamine hits.",
        "Do you even remember the last 5 reels you watched, {name}?",
        "{name}, your screen time is looking like a full-time job without a salary.",
        "Wake up, {name}. Real life is happening outside this glass rectangle.",
        "{name}, you're consuming content created by people who aren't wasting their time.",
        "Your discipline is on vacation, {name}. Time to call it back.",
        "{name}, doomscrolling is the highest form of self-sabotage.",
        "Tick tock, {name}. Time is the one asset you can never buy back.",
        "Hey {name}, how many more swipes until you feel satisfied?",
        "{name}, your ambition deserves better than a glowing screen.",
        "You called it 'just 5 minutes' half an hour ago, {name}.",
        "{name}, your goals are waiting for you to close this app.",
        "Look up, {name}. The world is far more interesting than this feed.",
        "{name}, you're letting an algorithm dictate your evening.",
        "Every swipe is a vote for mediocrity, {name}.",
        "Take a breath, {name}. Reclaim your mind from the feed.",
        "{name}, your focus is a superpower—stop giving it away for free.",
        "Hey {name}, the algorithm studied your weaknesses and it's winning.",
        "{name}, what great thing could you have started in the time you spent scrolling?",
        "Your thumb must be exhausted, {name}. Give it a break.",
        "{name}, you're swapping deep work for cheap entertainment.",
        "Shut it down, {name}. Rediscover peace of mind.",
        "{name}, true growth starts when the screen goes dark.",
        "Hey {name}, you're consuming life instead of creating it.",
        "{name}, your brain needs quiet time to process, not more noise.",
        "Don't let pixels dictate your mood, {name}.",
        "{name}, scrolling is just passive procrastination.",
        "Your destiny isn't hidden inside Instagram, {name}.",
        "{name}, the most successful version of you is offline right now.",
        "Hey {name}, take control of your time before the app takes control of you.",
        "{name}, you've swiped past a mile of content. Time to stop.",
        "Put the phone away, {name}, and do something you'll be proud of tonight.",
        "{name}, your focus is your currency—spend it wisely.",
        "Another 10 minutes sacrificed to the scroll gods, {name}.",
        "{name}, remember why you set up Unscroll in the first place.",
        "Take a quick rest, {name}. Focus on a distant point and breathe.",
        "{name}, close this app, take a deep breath, and conquer your day."
    )

    fun getInsult(index: Int): Insult {
        val safeIndex = kotlin.math.abs(index % INSULTS.size)
        return Insult(id = safeIndex, rawTemplate = INSULTS[safeIndex])
    }
}
