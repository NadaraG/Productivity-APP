package com.example.ui.components

import com.example.ui.viewmodel.AppLanguage

object Localization {
    fun get(key: String, lang: AppLanguage): String {
        return strings[key]?.get(lang) ?: key
    }

    private val strings = mapOf(
        // Tabs
        "tab_productivity" to mapOf(
            AppLanguage.ENGLISH to "Productivity",
            AppLanguage.GEORGIAN to "პროდუქტიულობა"
        ),
        "tab_fitness" to mapOf(
            AppLanguage.ENGLISH to "Fitness",
            AppLanguage.GEORGIAN to "ფიტნესი"
        ),
        "tab_smoking" to mapOf(
            AppLanguage.ENGLISH to "Smoking",
            AppLanguage.GEORGIAN to "მოწევა"
        ),

        // Main Dashboard Screen
        "dashboard_title" to mapOf(
            AppLanguage.ENGLISH to "Productivity Dashboard",
            AppLanguage.GEORGIAN to "პროდუქტიულობის პანელი"
        ),
        "dashboard_subtitle" to mapOf(
            AppLanguage.ENGLISH to "Here is your modern health and habit overview",
            AppLanguage.GEORGIAN to "აქ არის თქვენი ჯანმრთელობისა და ჩვევების მიმოხილვა"
        ),
        "habit_streak_title" to mapOf(
            AppLanguage.ENGLISH to "Habit Streak & Focus",
            AppLanguage.GEORGIAN to "ჩვევების სერია და ფოკუსი"
        ),
        "limit_not_exceeded" to mapOf(
            AppLanguage.ENGLISH to "You are currently matching your daily smoking limit!",
            AppLanguage.GEORGIAN to "თქვენ ამჟამად იცავთ მოწევის დღიურ ლიმიტს!"
        ),
        "limit_exceeded" to mapOf(
            AppLanguage.ENGLISH to "Daily smoking limit exceeded. Stay strong!",
            AppLanguage.GEORGIAN to "დღიური მოწევის ლიმიტი გადაჭარბებულია. გამაგრდით!"
        ),
        "weight_tracking" to mapOf(
            AppLanguage.ENGLISH to "Weight Tracking",
            AppLanguage.GEORGIAN to "წონის კონტროლი"
        ),
        "current_registered_weight" to mapOf(
            AppLanguage.ENGLISH to "Current registered weight",
            AppLanguage.GEORGIAN to "ამჟამინდელი დაფიქსირებული წონა"
        ),
        "no_weight_logs" to mapOf(
            AppLanguage.ENGLISH to "No weight logs recorded",
            AppLanguage.GEORGIAN to "წონის ჩანაწერები არ არის"
        ),
        "tap_to_record_weight" to mapOf(
            AppLanguage.ENGLISH to "Tap here to record your first weight measurement.",
            AppLanguage.GEORGIAN to "შეეხეთ აქ თქვენი პირველი წონის გასაზომად."
        ),
        "daily_smoking_counter" to mapOf(
            AppLanguage.ENGLISH to "Daily Smoking Counter",
            AppLanguage.GEORGIAN to "მოწევის დღიური მრიცხველი"
        ),
        "cigarettes_smoked_today" to mapOf(
            AppLanguage.ENGLISH to "Cigarettes Smoked Today",
            AppLanguage.GEORGIAN to "დღეს მოწეული სიგარეტი"
        ),
        "target_met" to mapOf(
            AppLanguage.ENGLISH to "Target Met",
            AppLanguage.GEORGIAN to "მიზანი შესრულდა"
        ),
        "target_exceeded" to mapOf(
            AppLanguage.ENGLISH to "Target Exceeded",
            AppLanguage.GEORGIAN to "მიზანი გადაჭარბდა"
        ),
        "today_cost" to mapOf(
            AppLanguage.ENGLISH to "Today's Cost",
            AppLanguage.GEORGIAN to "დღევანდელი ხარჯი"
        ),
        "log_plus_one" to mapOf(
            AppLanguage.ENGLISH to "Log +1",
            AppLanguage.GEORGIAN to "მონიშვნა +1"
        ),

        // Fitness Tracker
        "fitness_tracker_title" to mapOf(
            AppLanguage.ENGLISH to "Fitness Tracker",
            AppLanguage.GEORGIAN to "ფიტნეს ტრეკერი"
        ),
        "fitness_tracker_subtitle" to mapOf(
            AppLanguage.ENGLISH to "Monitor fitness metrics & trends",
            AppLanguage.GEORGIAN to "აკონტროლეთ ფიტნესის მაჩვენებლები და ტენდენციები"
        ),
        "track_fitness_changes" to mapOf(
            AppLanguage.ENGLISH to "Track Fitness Changes",
            AppLanguage.GEORGIAN to "ფიტნესის ცვლილებების კონტროლი"
        ),
        "history" to mapOf(
            AppLanguage.ENGLISH to "History",
            AppLanguage.GEORGIAN to "ისტორია"
        ),
        "progress" to mapOf(
            AppLanguage.ENGLISH to "Progress",
            AppLanguage.GEORGIAN to "პროგრესი"
        ),
        "no_logs_available" to mapOf(
            AppLanguage.ENGLISH to "No logs available",
            AppLanguage.GEORGIAN to "ჩანაწერები არ არის"
        ),
        "compare_logs_tip" to mapOf(
            AppLanguage.ENGLISH to "Compare with older logs to see your transformation progress here.",
            AppLanguage.GEORGIAN to "შეადარეთ ძველ ჩანაწერებს თქვენი ტრანსფორმაციის პროგრესის სანახავად."
        ),
        "record_measurements" to mapOf(
            AppLanguage.ENGLISH to "Record measurements",
            AppLanguage.GEORGIAN to "მონაცემების ჩაწერა"
        ),
        "populate_demo_data" to mapOf(
            AppLanguage.ENGLISH to "Populate Demo Data",
            AppLanguage.GEORGIAN to "დემო მონაცემების შევსება"
        ),
        "load_charts_tip" to mapOf(
            AppLanguage.ENGLISH to "Let's load beautiful progress visualizer charts",
            AppLanguage.GEORGIAN to "ჩავტვირთოთ პროგრესის ლამაზი ვიზუალიზატორი"
        ),
        "step" to mapOf(
            AppLanguage.ENGLISH to "Step",
            AppLanguage.GEORGIAN to "ნაბიჯი"
        ),
        "cancel" to mapOf(
            AppLanguage.ENGLISH to "Cancel",
            AppLanguage.GEORGIAN to "გაუქმება"
        ),
        "back" to mapOf(
            AppLanguage.ENGLISH to "Back",
            AppLanguage.GEORGIAN to "უკან"
        ),
        "save_and_exit" to mapOf(
            AppLanguage.ENGLISH to "Save & Exit",
            AppLanguage.GEORGIAN to "შენახვა და გასვლა"
        ),
        "next_step" to mapOf(
            AppLanguage.ENGLISH to "Next Step",
            AppLanguage.GEORGIAN to "შემდეგი"
        ),
        "notes" to mapOf(
            AppLanguage.ENGLISH to "Notes",
            AppLanguage.GEORGIAN to "შენიშვნები"
        ),
        "finish_and_save" to mapOf(
            AppLanguage.ENGLISH to "Finish & Save",
            AppLanguage.GEORGIAN to "დასრულება და შენახვა"
        ),
        "add_log" to mapOf(
            AppLanguage.ENGLISH to "Add Log",
            AppLanguage.GEORGIAN to "ჩანაწერის დამატება"
        ),
        "log_notes_and_context" to mapOf(
            AppLanguage.ENGLISH to "Log Notes & Context",
            AppLanguage.GEORGIAN to "შენიშვნები და კონტექსტი"
        ),
        "notes_placeholder" to mapOf(
            AppLanguage.ENGLISH to "How do you feel today? Any dietary changes, fatigue level...",
            AppLanguage.GEORGIAN to "როგორ გრძნობთ თავს დღეს? კვების ცვლილებები, დაღლილობის დონე..."
        ),
        "save_log" to mapOf(
            AppLanguage.ENGLISH to "Save Log",
            AppLanguage.GEORGIAN to "ჩანაწერის შენახვა"
        ),
        "close" to mapOf(
            AppLanguage.ENGLISH to "Close",
            AppLanguage.GEORGIAN to "დახურვა"
        ),

        // Metrics
        "Weight" to mapOf(
            AppLanguage.ENGLISH to "Weight",
            AppLanguage.GEORGIAN to "წონა"
        ),
        "Chest" to mapOf(
            AppLanguage.ENGLISH to "Chest",
            AppLanguage.GEORGIAN to "გულმკერდი"
        ),
        "Waist" to mapOf(
            AppLanguage.ENGLISH to "Waist",
            AppLanguage.GEORGIAN to "წელი"
        ),
        "Hips" to mapOf(
            AppLanguage.ENGLISH to "Hips",
            AppLanguage.GEORGIAN to "თეძოები"
        ),
        "Left Bicep" to mapOf(
            AppLanguage.ENGLISH to "Left Bicep",
            AppLanguage.GEORGIAN to "მარცხენა ბიცეფსი"
        ),
        "Right Bicep" to mapOf(
            AppLanguage.ENGLISH to "Right Bicep",
            AppLanguage.GEORGIAN to "მარჯვენა ბიცეფსი"
        ),
        "Left Forearm" to mapOf(
            AppLanguage.ENGLISH to "Left Forearm",
            AppLanguage.GEORGIAN to "მარცხენა წინამხარი"
        ),
        "Right Forearm" to mapOf(
            AppLanguage.ENGLISH to "Right Forearm",
            AppLanguage.GEORGIAN to "მარჯვენა წინამხარი"
        ),
        "Left Thigh" to mapOf(
            AppLanguage.ENGLISH to "Left Thigh",
            AppLanguage.GEORGIAN to "მარცხენა ბარძაყი"
        ),
        "Right Thigh" to mapOf(
            AppLanguage.ENGLISH to "Right Thigh",
            AppLanguage.GEORGIAN to "მარჯვენა ბარძაყი"
        ),
        "Left Calf" to mapOf(
            AppLanguage.ENGLISH to "Left Calf",
            AppLanguage.GEORGIAN to "მარცხენა წვივი"
        ),
        "Right Calf" to mapOf(
            AppLanguage.ENGLISH to "Right Calf",
            AppLanguage.GEORGIAN to "მარჯვენა წვივი"
        ),

        // Smoking Tracker
        "smoking_tracker_title" to mapOf(
            AppLanguage.ENGLISH to "Smoking Tracker",
            AppLanguage.GEORGIAN to "მოწევის ტრეკერი"
        ),
        "smoking_tracker_subtitle" to mapOf(
            AppLanguage.ENGLISH to "Track habits and financial savings",
            AppLanguage.GEORGIAN to "აკონტროლეთ ჩვევები და ფინანსური დანაზოგი"
        ),
        "smoke_a_cigarette" to mapOf(
            AppLanguage.ENGLISH to "Smoke a cigarette",
            AppLanguage.GEORGIAN to "სიგარეტის მოწევა"
        ),
        "record_custom_quantity" to mapOf(
            AppLanguage.ENGLISH to "Record custom quantity",
            AppLanguage.GEORGIAN to "სხვა რაოდენობის ჩაწერა"
        ),
        "parameters" to mapOf(
            AppLanguage.ENGLISH to "Parameters",
            AppLanguage.GEORGIAN to "პარამეტრები"
        ),
        "smoke" to mapOf(
            AppLanguage.ENGLISH to "Smoke",
            AppLanguage.GEORGIAN to "მოწევა"
        ),
        "log_custom_quantity" to mapOf(
            AppLanguage.ENGLISH to "Log Custom Quantity",
            AppLanguage.GEORGIAN to "სხვა რაოდენობის ჩაწერა"
        ),
        "how_many_cigarettes_question" to mapOf(
            AppLanguage.ENGLISH to "How many cigarettes did you smoke?",
            AppLanguage.GEORGIAN to "რამდენი სიგარეტი მოწიეთ?"
        ),
        "quantity" to mapOf(
            AppLanguage.ENGLISH to "Quantity",
            AppLanguage.GEORGIAN to "რაოდენობა"
        ),
        "save" to mapOf(
            AppLanguage.ENGLISH to "Save",
            AppLanguage.GEORGIAN to "შენახვა"
        ),
        "dismiss" to mapOf(
            AppLanguage.ENGLISH to "Dismiss",
            AppLanguage.GEORGIAN to "დახურვა"
        ),
        "today" to mapOf(
            AppLanguage.ENGLISH to "Today",
            AppLanguage.GEORGIAN to "დღეს"
        ),
        "weekly" to mapOf(
            AppLanguage.ENGLISH to "Weekly",
            AppLanguage.GEORGIAN to "კვირეული"
        ),
        "monthly" to mapOf(
            AppLanguage.ENGLISH to "Monthly",
            AppLanguage.GEORGIAN to "ყოველთვიური"
        ),
        "smoking_parameters_and_targets" to mapOf(
            AppLanguage.ENGLISH to "Smoking Parameters & Targets",
            AppLanguage.GEORGIAN to "მოწევის პარამეტრები და მიზნები"
        ),
        "price_of_pack" to mapOf(
            AppLanguage.ENGLISH to "Price of a Pack (₾)",
            AppLanguage.GEORGIAN to "კოლოფის ფასი (₾)"
        ),
        "cigarettes_in_pack" to mapOf(
            AppLanguage.ENGLISH to "Cigarettes in a Pack",
            AppLanguage.GEORGIAN to "სიგარეტის რაოდენობა კოლოფში"
        ),
        "daily_target_limit" to mapOf(
            AppLanguage.ENGLISH to "Daily Target Limit",
            AppLanguage.GEORGIAN to "დღიური მიზანი (სიგარეტი)"
        ),
        "monthly_target_limit" to mapOf(
            AppLanguage.ENGLISH to "Monthly Target Limit",
            AppLanguage.GEORGIAN to "ყოველთვიური მიზანი (სიგარეტი)"
        ),
        "apply_settings" to mapOf(
            AppLanguage.ENGLISH to "Apply Settings",
            AppLanguage.GEORGIAN to "პარამეტრების გამოყენება"
        ),
        "total_spent_this_month" to mapOf(
            AppLanguage.ENGLISH to "Total Spent This Month",
            AppLanguage.GEORGIAN to "ჯამური ხარჯი ამ თვეში"
        ),
        "cigarettes_smoked" to mapOf(
            AppLanguage.ENGLISH to "Cigarettes Smoked",
            AppLanguage.GEORGIAN to "მოწეული სიგარეტები"
        ),
        "money_spent" to mapOf(
            AppLanguage.ENGLISH to "Money Spent",
            AppLanguage.GEORGIAN to "დახარჯული თანხა"
        ),
        "no_logs_recorded_today" to mapOf(
            AppLanguage.ENGLISH to "No logs recorded today",
            AppLanguage.GEORGIAN to "დღეს ჩანაწერები არ არის"
        ),
        "logged" to mapOf(
            AppLanguage.ENGLISH to "Logged",
            AppLanguage.GEORGIAN to "დაფიქსირდა"
        ),
        "cigarettes" to mapOf(
            AppLanguage.ENGLISH to "cigarettes",
            AppLanguage.GEORGIAN to "სიგარეტი"
        ),
        "cost_label" to mapOf(
            AppLanguage.ENGLISH to "Cost",
            AppLanguage.GEORGIAN to "ხარჯი"
        ),
        "history_of_smoking" to mapOf(
            AppLanguage.ENGLISH to "History of Smoking",
            AppLanguage.GEORGIAN to "მოწევის ისტორია"
        ),
        "clear_all" to mapOf(
            AppLanguage.ENGLISH to "Clear All",
            AppLanguage.GEORGIAN to "ყველას წაშლა"
        )
    )
}
