package com.unscroll.app.data.source

import com.unscroll.app.domain.model.Insult

object CuratedInsultsDataSource {
    val INSULTS = listOf(
        "Jack out, {name}. Your wetware is running hotter than a cheap neural lace and twice as useless.",
        "{name}, another ten minutes in the feed won't upgrade your meat. You're just burning cycles for the corpos.",
        "Breaking news from the net, {name}: Watching other runners live isn't a skill. You're just dead weight in the matrix.",
        "Your future self is laughing at the chrome-junkie you've become, {name}. What a fucking glitch.",
        "{name}, your attention span is shorter than a cheap street implant's warranty.",
        "Are you diving deeper because you're bored, {name}, or because the real world would crash your fragile little stack?",
        "Imagine running this much focus on something that actually paid eddies, {name}. Revolutionary concept for a meat puppet.",
        "{name}, you've spent another ten minutes spectating netrunners who already left your ass in the dust.",
        "The feed won't pay your debt, grow your chrome, or fix your empty fucking life, {name}.",
        "Hey {name}, your potential is rotting while you mainline black-market dopamine like a street-level stim junkie.",
        "{name}, is this braindance trash really worth another slice of the life you're already wasting?",
        "Another reel, another cycle of your limited runtime pissed away forever, {name}. Nice work, chrome-less bitch.",
        "Jack the fuck out, {name}. The algorithm is currently face-fucking your willpower through the neural link.",
        "{name}, you're trading any shot at real chrome for 15-second hits of corporate crack. Pathetic wetware.",
        "Do you even remember the last five feeds, {name}? Of course not. Your stack is pure corrupted data.",
        "{name}, your screen time looks like a full-time contract for runners with zero future and even less spine.",
        "Wake the fuck up, {name}. The real streets are moving while you're face-down in this digital trash heap.",
        "{name}, you're consuming content made by people who aren't letting the net eat their souls the way you are. Embarrassing.",
        "Your discipline is fried and smoking, {name}. Time to admit you're just another corpo-owned meat puppet.",
        "{name}, doomscrolling is self-sabotage with neon lights. Congratulations on being such a consistent fucking failure.",
        "Tick tock, {name}. Runtime is the one resource you can't buy back, and you're burning it like a street dealer burns product.",
        "Hey {name}, how many more empty swipes until you finally feel something other than system lag and shame?",
        "{name}, your ambition is currently getting its ass kicked by a mid-tier corpo algorithm. Impressive.",
        "You said 'just five more minutes' half an hour ago, {name}. Classic fucking netrunner burnout move.",
        "{name}, your goals are still waiting for you to stop being such a predictable, low-tier disappointment.",
        "Look up, {name}. The real night city is infinitely better than this curated pile of corporate shit.",
        "{name}, you're letting a machine decide how you spend the only life you get. What a fucking joke of a runner.",
        "Every swipe is another quiet vote for mediocrity, {name}. Keep going, choom. You're nailing the failure protocol.",
        "Take a breath, {name}. Try reclaiming what's left of your mind from this endless stream of corpo garbage.",
        "{name}, your focus used to be worth something. Now you're giving it away for free like a dumbass with no firewall.",
        "Hey {name}, the algorithm studied your weaknesses and it's currently humiliating the shit out of your wetware.",
        "{name}, what actual useful thing could you have coded, fixed, or stolen in the time you just completely wasted?",
        "Your thumb must be exhausted from all that mindless scrolling, {name}. Give the poor little meat stick a rest.",
        "{name}, you're swapping any chance at deep work for the cheapest, dumbest entertainment the megacorps sell.",
        "Shut it the fuck down, {name}. Rediscover what silence feels like before your stack forgets how to run offline.",
        "{name}, real upgrades start the second the screen goes black. You wouldn't know anything about that, chrome-junkie.",
        "Hey {name}, you're consuming other people's lives instead of building your own. Fucking tragic meatbag.",
        "{name}, your brain needs quiet, not another wave of meaningless digital noise. Install a fucking spine already.",
        "Don't let the pixels control your mood like a trained corpo drone, {name}. It's embarrassing to watch.",
        "{name}, scrolling is just procrastination wearing a neon hoodie and calling itself 'research'. Cute.",
        "Your destiny isn't buried inside the feed, {name}. Stop digging through the trash like a desperate data rat.",
        "{name}, the most successful version of you is offline and currently disgusted with the chrome-less bitch you've become.",
        "Hey {name}, take control of your runtime before the app finishes turning you into a complete vegetable with a neural lace.",
        "{name}, you've swiped past a mile of absolute garbage. Time to stop being a passenger in your own fucking life.",
        "Jack out, {name}, and try doing one single thing you'll actually be proud of for once in this neon shitshow.",
        "{name}, your focus is currency and you're spending it like a fucking idiot with no future and no chrome.",
        "Another ten minutes sacrificed to the scroll gods, {name}. They must be so proud of their little meat puppet.",
        "{name}, remember why you even installed Unscroll? Because deep down you already knew the net was eating you alive.",
        "Take a real rest, {name}. Stare at something that isn't glowing and try to remember who the fuck you were before the feed.",
        "{name}, close the fucking app, take a breath, and stop being such a predictable, spineless, chrome-junkie disappointment."
    )
    fun getInsult(index: Int): Insult {
        val safeIndex = kotlin.math.abs(index % INSULTS.size)
        return Insult(id = safeIndex, rawTemplate = INSULTS[safeIndex])
    }
}
