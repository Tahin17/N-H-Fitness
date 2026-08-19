package com.aegisfit.app.data.seed

import androidx.sqlite.db.SupportSQLiteDatabase

object SeedData {
    fun seedWorkoutDays(db: SupportSQLiteDatabase) {
        // workout days
        db.execSQL("INSERT INTO workout_days (day_number, name, muscle_groups) VALUES (1, 'Chest & Triceps', 'Chest,Triceps');")
        db.execSQL("INSERT INTO workout_days (day_number, name, muscle_groups) VALUES (2, 'Back & Biceps', 'Back,Biceps');")
        db.execSQL("INSERT INTO workout_days (day_number, name, muscle_groups) VALUES (3, 'Shoulders & Abs', 'Shoulders,Abs');")
        db.execSQL("INSERT INTO workout_days (day_number, name, muscle_groups) VALUES (4, 'Legs', 'Quads,Glutes,Calves');")
        db.execSQL("INSERT INTO workout_days (day_number, name, muscle_groups) VALUES (5, 'Midsection & Symmetry', 'Core,FullBody');")

        // exercises
        // Day 1
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (1, 'Flat Barbell Bench Press', 'Chest', 1, 0, NULL);")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (1, 'Incline Dumbbell Press', 'Upper Chest', 2, 0, NULL);")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (1, 'Cable Fly', 'Chest', 3, 0, NULL);")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (1, 'Tricep Dips', 'Triceps', 4, 0, NULL);")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (1, 'Tricep Rope Pushdown', 'Triceps', 5, 0, NULL);")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (1, 'Overhead Tricep Extension', 'Triceps', 6, 0, NULL);")

        // Day 2
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (2, 'Conventional Deadlift', 'Back', 1, 0, NULL);")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (2, 'Lat Pulldown', 'Lats', 2, 0, NULL);")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (2, 'Seated Cable Row', 'Back', 3, 0, NULL);")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (2, 'Face Pull', 'Rear Delts', 4, 0, NULL);")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (2, 'Barbell Curl', 'Biceps', 5, 0, NULL);")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (2, 'Hammer Curl', 'Biceps', 6, 0, NULL);")

        // Day 3
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (3, 'Overhead Press', 'Shoulders', 1, 0, NULL);")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (3, 'Lateral Raise', 'Side Delts', 2, 0, NULL);")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (3, 'Front Raise', 'Front Delts', 3, 0, NULL);")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (3, 'Rear Delt Fly', 'Rear Delts', 4, 0, NULL);")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (3, 'Hanging Leg Raise', 'Abs', 5, 0, NULL);")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (3, 'Cable Crunch', 'Abs', 6, 0, NULL);")

        // Day 4
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (4, 'Barbell Squat', 'Quads', 1, 0, NULL);")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (4, 'Leg Press', 'Quads', 2, 0, NULL);")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (4, 'Romanian Deadlift', 'Hamstrings', 3, 0, NULL);")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (4, 'Leg Extension', 'Quads', 4, 0, NULL);")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (4, 'Leg Curl', 'Hamstrings', 5, 0, NULL);")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (4, 'Standing Calf Raise', 'Calves', 6, 0, NULL);")

        // Day 5
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (5, 'Single-Arm Dumbbell Row', 'Back', 1, 1, 'Start with left side');")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (5, 'Single-Leg Romanian Deadlift', 'Hamstrings', 2, 1, 'Start with left side');")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (5, 'Single-Arm Overhead Press', 'Shoulders', 3, 1, 'Start with left side');")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (5, 'Oblique Crunch', 'Obliques', 4, 1, 'Start with left side');")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (5, 'Plank Hold', 'Core', 5, 0, '60 seconds');")
        db.execSQL("INSERT INTO exercises (workout_day_id, name, target_muscle, order_in_day, is_unilateral, notes) VALUES (5, 'Farmer Walk', 'FullBody', 6, 0, '40 meters');")
    }

    fun seedFoodItems(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TEMP TABLE IF NOT EXISTS temp_food_items (name TEXT, brand TEXT, calories_per_100g REAL, protein_per_100g REAL, carbs_per_100g REAL, fat_per_100g REAL, fiber_per_100g REAL, default_serving_size_g REAL, serving_description TEXT, barcode TEXT, is_local_bd INTEGER, category TEXT, image_url TEXT);")
        val insertPrefix = "INSERT INTO temp_food_items (name, brand, calories_per_100g, protein_per_100g, carbs_per_100g, fat_per_100g, fiber_per_100g, default_serving_size_g, serving_description, barcode, is_local_bd, category, image_url) VALUES "
        
        // Rice Dishes
        db.execSQL("$insertPrefix ('Kacchi Biryani', NULL, 180, 8.0, 22.0, 7.0, 0.5, 300.0, 'per plate', NULL, 1, 'Rice', NULL);")
        db.execSQL("$insertPrefix ('Chicken Biryani', NULL, 165, 9.0, 21.0, 5.0, 0.4, 300.0, 'per plate', NULL, 1, 'Rice', NULL);")
        db.execSQL("$insertPrefix ('Plain Rice (Sada Bhat)', NULL, 130, 2.7, 28.0, 0.3, 0.4, 200.0, 'per bowl', NULL, 1, 'Rice', NULL);")
        db.execSQL("$insertPrefix ('Bhuna Khichuri', NULL, 150, 5.0, 20.0, 5.5, 1.2, 250.0, 'per bowl', NULL, 1, 'Rice', NULL);")
        db.execSQL("$insertPrefix ('Panta Bhat', NULL, 110, 2.0, 24.0, 0.2, 0.3, 200.0, 'per bowl', NULL, 1, 'Rice', NULL);")
        db.execSQL("$insertPrefix ('Tehari', NULL, 170, 7.0, 23.0, 5.0, 0.5, 300.0, 'per plate', NULL, 1, 'Rice', NULL);")
        db.execSQL("$insertPrefix ('Pulao', NULL, 160, 4.0, 24.0, 5.0, 0.5, 200.0, 'per bowl', NULL, 1, 'Rice', NULL);")
        db.execSQL("$insertPrefix ('Khichuri (Bhuna)', NULL, 145, 5.0, 20.0, 5.0, 1.5, 250.0, 'per bowl', NULL, 1, 'Rice', NULL);")

        // Bread/Roti
        db.execSQL("$insertPrefix ('Roti (Plain)', NULL, 300, 9.0, 50.0, 7.0, 3.0, 40.0, 'per piece', NULL, 1, 'Bread', NULL);")
        db.execSQL("$insertPrefix ('Paratha (Plain)', NULL, 320, 6.0, 38.0, 16.0, 2.0, 60.0, 'per piece', NULL, 1, 'Bread', NULL);")
        db.execSQL("$insertPrefix ('Luchi', NULL, 340, 5.0, 42.0, 17.0, 1.0, 30.0, 'per piece', NULL, 1, 'Bread', NULL);")
        db.execSQL("$insertPrefix ('Naan', NULL, 290, 8.0, 48.0, 7.0, 2.0, 90.0, 'per piece', NULL, 1, 'Bread', NULL);")
        db.execSQL("$insertPrefix ('Porota (Dhakai)', NULL, 350, 6.0, 40.0, 18.0, 1.5, 80.0, 'per piece', NULL, 1, 'Bread', NULL);")

        // Curries & Mains
        db.execSQL("$insertPrefix ('Chicken Bhuna', NULL, 180, 20.0, 5.0, 9.0, 0.8, 150.0, 'per serving', NULL, 1, 'Curry', NULL);")
        db.execSQL("$insertPrefix ('Beef Bhuna', NULL, 200, 19.0, 4.0, 12.0, 0.5, 150.0, 'per serving', NULL, 1, 'Curry', NULL);")
        db.execSQL("$insertPrefix ('Mutton Bhuna', NULL, 210, 18.0, 3.0, 14.0, 0.5, 150.0, 'per serving', NULL, 1, 'Curry', NULL);")
        db.execSQL("$insertPrefix ('Hilsha Curry (Ilish)', NULL, 200, 18.0, 3.0, 13.0, 0.0, 150.0, 'per serving', NULL, 1, 'Curry', NULL);")
        db.execSQL("$insertPrefix ('Rohu Fish Curry', NULL, 120, 16.0, 4.0, 5.0, 0.3, 150.0, 'per serving', NULL, 1, 'Curry', NULL);")
        db.execSQL("$insertPrefix ('Prawn Curry (Chingri)', NULL, 110, 18.0, 3.0, 3.0, 0.2, 150.0, 'per serving', NULL, 1, 'Curry', NULL);")
        db.execSQL("$insertPrefix ('Egg Curry (Dim Torkari)', NULL, 150, 10.0, 6.0, 10.0, 0.5, 150.0, 'per serving', NULL, 1, 'Curry', NULL);")
        db.execSQL("$insertPrefix ('Chicken Rezala', NULL, 170, 16.0, 5.0, 10.0, 0.5, 150.0, 'per serving', NULL, 1, 'Curry', NULL);")
        db.execSQL("$insertPrefix ('Beef Kala Bhuna', NULL, 195, 18.0, 5.0, 11.0, 0.6, 150.0, 'per serving', NULL, 1, 'Curry', NULL);")
        db.execSQL("$insertPrefix ('Doi Maach', NULL, 130, 14.0, 5.0, 6.0, 0.2, 150.0, 'per serving', NULL, 1, 'Curry', NULL);")
        db.execSQL("$insertPrefix ('Chicken Kosha', NULL, 175, 19.0, 4.0, 9.0, 0.5, 150.0, 'per serving', NULL, 1, 'Curry', NULL);")
        db.execSQL("$insertPrefix ('Shorshe Ilish', NULL, 190, 17.0, 3.0, 12.0, 0.2, 150.0, 'per serving', NULL, 1, 'Curry', NULL);")

        // Dals & Lentils
        db.execSQL("$insertPrefix ('Masoor Dal', NULL, 116, 9.0, 20.0, 0.5, 4.0, 200.0, 'per bowl', NULL, 1, 'Dal', NULL);")
        db.execSQL("$insertPrefix ('Moong Dal', NULL, 105, 7.0, 18.0, 0.5, 3.5, 200.0, 'per bowl', NULL, 1, 'Dal', NULL);")
        db.execSQL("$insertPrefix ('Chola Dal', NULL, 120, 8.0, 20.0, 1.5, 5.0, 200.0, 'per bowl', NULL, 1, 'Dal', NULL);")
        db.execSQL("$insertPrefix ('Moshur Dal', NULL, 110, 8.0, 19.0, 0.4, 4.0, 200.0, 'per bowl', NULL, 1, 'Dal', NULL);")
        db.execSQL("$insertPrefix ('Dal Bhat (Mixed)', NULL, 108, 7.0, 19.0, 0.5, 3.0, 200.0, 'per bowl', NULL, 1, 'Dal', NULL);")

        // Bhortas (Mashed dishes)
        db.execSQL("$insertPrefix ('Alu Bharta', NULL, 100, 2.0, 15.0, 4.0, 1.5, 100.0, 'per serving', NULL, 1, 'Bharta', NULL);")
        db.execSQL("$insertPrefix ('Begun Bharta', NULL, 80, 1.5, 10.0, 4.0, 2.0, 100.0, 'per serving', NULL, 1, 'Bharta', NULL);")
        db.execSQL("$insertPrefix ('Shutki Bharta', NULL, 200, 30.0, 3.0, 8.0, 0.5, 50.0, 'per serving', NULL, 1, 'Bharta', NULL);")
        db.execSQL("$insertPrefix ('Dal Bharta', NULL, 120, 8.0, 16.0, 3.0, 4.0, 100.0, 'per serving', NULL, 1, 'Bharta', NULL);")
        db.execSQL("$insertPrefix ('Tomato Bharta', NULL, 60, 1.0, 8.0, 3.0, 1.5, 100.0, 'per serving', NULL, 1, 'Bharta', NULL);")
        db.execSQL("$insertPrefix ('Chingri Bharta', NULL, 130, 15.0, 3.0, 7.0, 0.3, 80.0, 'per serving', NULL, 1, 'Bharta', NULL);")

        // Vegetables
        db.execSQL("$insertPrefix ('Shobji (Mixed Veg Curry)', NULL, 65, 2.0, 8.0, 3.0, 2.5, 150.0, 'per serving', NULL, 1, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Aloo Gobi', NULL, 80, 2.5, 10.0, 3.5, 2.0, 150.0, 'per serving', NULL, 1, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Shim Bhaji (Flat Beans)', NULL, 50, 3.0, 7.0, 1.5, 3.0, 150.0, 'per serving', NULL, 1, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Pui Shak', NULL, 30, 2.0, 4.0, 0.5, 2.0, 100.0, 'per serving', NULL, 1, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Lau Ghonto', NULL, 45, 1.5, 6.0, 2.0, 1.0, 150.0, 'per serving', NULL, 1, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Korola Bhaji (Bitter Gourd)', NULL, 40, 1.0, 5.0, 2.0, 2.5, 100.0, 'per serving', NULL, 1, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Potol Bhaji', NULL, 50, 1.5, 6.0, 2.5, 2.0, 100.0, 'per serving', NULL, 1, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Data Shak (Stem Amaranth)', NULL, 25, 2.0, 3.0, 0.3, 2.0, 100.0, 'per serving', NULL, 1, 'Vegetable', NULL);")

        // Street Food & Snacks
        db.execSQL("$insertPrefix ('Chotpoti', NULL, 150, 5.0, 22.0, 5.0, 3.0, 200.0, 'per bowl', NULL, 1, 'StreetFood', NULL);")
        db.execSQL("$insertPrefix ('Fuchka', NULL, 25, 0.8, 4.0, 0.5, 0.3, 30.0, 'per piece', NULL, 1, 'StreetFood', NULL);")
        db.execSQL("$insertPrefix ('Singara (Samosa)', NULL, 280, 5.0, 30.0, 15.0, 2.0, 60.0, 'per piece', NULL, 1, 'StreetFood', NULL);")
        db.execSQL("$insertPrefix ('Piyaju (Lentil Fritter)', NULL, 300, 8.0, 28.0, 18.0, 3.0, 40.0, 'per piece', NULL, 1, 'StreetFood', NULL);")
        db.execSQL("$insertPrefix ('Beguni (Eggplant Fritter)', NULL, 250, 3.0, 25.0, 15.0, 2.0, 40.0, 'per piece', NULL, 1, 'StreetFood', NULL);")
        db.execSQL("$insertPrefix ('Jhalmuri', NULL, 320, 8.0, 50.0, 10.0, 3.0, 100.0, 'per serving', NULL, 1, 'StreetFood', NULL);")
        db.execSQL("$insertPrefix ('Chanachur', NULL, 450, 10.0, 52.0, 22.0, 3.0, 50.0, 'per serving', NULL, 1, 'StreetFood', NULL);")
        db.execSQL("$insertPrefix ('Pakora', NULL, 290, 5.0, 28.0, 18.0, 2.0, 40.0, 'per piece', NULL, 1, 'StreetFood', NULL);")
        db.execSQL("$insertPrefix ('Egg Roll', NULL, 220, 10.0, 22.0, 11.0, 1.0, 150.0, 'per piece', NULL, 1, 'StreetFood', NULL);")
        db.execSQL("$insertPrefix ('Chicken Roll', NULL, 230, 12.0, 24.0, 10.0, 1.0, 150.0, 'per piece', NULL, 1, 'StreetFood', NULL);")

        // Sweets & Desserts
        db.execSQL("$insertPrefix ('Roshogolla', NULL, 180, 4.0, 30.0, 5.0, 0.0, 50.0, 'per piece', NULL, 1, 'Sweet', NULL);")
        db.execSQL("$insertPrefix ('Mishti Doi', NULL, 150, 4.0, 22.0, 5.0, 0.0, 100.0, 'per cup', NULL, 1, 'Sweet', NULL);")
        db.execSQL("$insertPrefix ('Chomchom', NULL, 200, 5.0, 32.0, 6.0, 0.0, 60.0, 'per piece', NULL, 1, 'Sweet', NULL);")
        db.execSQL("$insertPrefix ('Jilapi (Jalebi)', NULL, 370, 3.0, 56.0, 15.0, 0.0, 50.0, 'per piece', NULL, 1, 'Sweet', NULL);")
        db.execSQL("$insertPrefix ('Firni', NULL, 140, 3.5, 20.0, 5.0, 0.3, 120.0, 'per bowl', NULL, 1, 'Sweet', NULL);")
        db.execSQL("$insertPrefix ('Shandesh', NULL, 240, 6.0, 35.0, 9.0, 0.0, 40.0, 'per piece', NULL, 1, 'Sweet', NULL);")
        db.execSQL("$insertPrefix ('Kalojam', NULL, 350, 5.0, 50.0, 15.0, 0.0, 50.0, 'per piece', NULL, 1, 'Sweet', NULL);")
        db.execSQL("$insertPrefix ('Payesh', NULL, 130, 4.0, 18.0, 4.5, 0.2, 150.0, 'per bowl', NULL, 1, 'Sweet', NULL);")
        db.execSQL("$insertPrefix ('Shemai', NULL, 160, 4.0, 24.0, 5.0, 0.3, 120.0, 'per bowl', NULL, 1, 'Sweet', NULL);")
        db.execSQL("$insertPrefix ('Zarda (Sweet Rice)', NULL, 180, 2.0, 32.0, 5.0, 0.2, 150.0, 'per serving', NULL, 1, 'Sweet', NULL);")

        // Beverages
        db.execSQL("$insertPrefix ('Cha (Milk Tea)', NULL, 50, 1.5, 7.0, 1.5, 0.0, 150.0, 'per cup', NULL, 1, 'Beverage', NULL);")
        db.execSQL("$insertPrefix ('Lassi (Sweet)', NULL, 70, 3.0, 10.0, 2.0, 0.0, 200.0, 'per glass', NULL, 1, 'Beverage', NULL);")
        db.execSQL("$insertPrefix ('Borhani', NULL, 25, 1.0, 3.0, 1.0, 0.0, 200.0, 'per glass', NULL, 1, 'Beverage', NULL);")
        db.execSQL("$insertPrefix ('Mango Lassi', NULL, 80, 3.0, 12.0, 2.5, 0.3, 200.0, 'per glass', NULL, 1, 'Beverage', NULL);")
        db.execSQL("$insertPrefix ('Sugarcane Juice (Akher Rosh)', NULL, 40, 0.0, 10.0, 0.0, 0.0, 250.0, 'per glass', NULL, 1, 'Beverage', NULL);")
        db.execSQL("$insertPrefix ('Lemon Sharbat', NULL, 35, 0.0, 9.0, 0.0, 0.0, 250.0, 'per glass', NULL, 1, 'Beverage', NULL);")

        // Eggs & Dairy
        db.execSQL("$insertPrefix ('Boiled Egg', NULL, 155, 13.0, 1.0, 11.0, 0.0, 50.0, 'per egg', NULL, 1, 'EggDairy', NULL);")
        db.execSQL("$insertPrefix ('Egg Omelette (Oil)', NULL, 195, 11.0, 1.5, 16.0, 0.0, 60.0, 'per egg', NULL, 1, 'EggDairy', NULL);")
        db.execSQL("$insertPrefix ('Paneer (Ponir)', NULL, 265, 18.0, 3.0, 20.0, 0.0, 50.0, 'per serving', NULL, 1, 'EggDairy', NULL);")

        // Common Proteins
        db.execSQL("$insertPrefix ('Chicken Breast (Grilled)', NULL, 165, 31.0, 0.0, 3.6, 0.0, 150.0, 'per piece', NULL, 1, 'Protein', NULL);")
        db.execSQL("$insertPrefix ('Chicken Thigh (Cooked)', NULL, 210, 26.0, 0.0, 11.0, 0.0, 120.0, 'per piece', NULL, 1, 'Protein', NULL);")
        db.execSQL("$insertPrefix ('Beef (Cooked Lean)', NULL, 250, 26.0, 0.0, 15.0, 0.0, 120.0, 'per serving', NULL, 1, 'Protein', NULL);")
        db.execSQL("$insertPrefix ('Tilapia (Cooked)', NULL, 128, 26.0, 0.0, 2.5, 0.0, 120.0, 'per piece', NULL, 1, 'Protein', NULL);")
        db.execSQL("$insertPrefix ('Whey Protein (Scoop)', NULL, 380, 75.0, 8.0, 5.0, 0.0, 30.0, 'per scoop', NULL, 1, 'Protein', NULL);")

        // Fruits
        db.execSQL("$insertPrefix ('Banana (Kola)', NULL, 89, 1.1, 23.0, 0.3, 2.6, 120.0, 'per piece', NULL, 1, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Mango (Aam)', NULL, 60, 0.8, 15.0, 0.4, 1.6, 150.0, 'per piece', NULL, 1, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Jackfruit (Kathal)', NULL, 95, 1.7, 23.0, 0.6, 1.5, 100.0, 'per serving', NULL, 1, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Guava (Peyara)', NULL, 68, 2.6, 14.0, 1.0, 5.4, 100.0, 'per piece', NULL, 1, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Papaya (Pepe)', NULL, 43, 0.5, 11.0, 0.3, 1.7, 150.0, 'per serving', NULL, 1, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Watermelon (Tormuz)', NULL, 30, 0.6, 7.6, 0.2, 0.4, 200.0, 'per slice', NULL, 1, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Lychee (Lichu)', NULL, 66, 0.8, 17.0, 0.4, 1.3, 100.0, 'per serving', NULL, 1, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Coconut (Narikel)', NULL, 354, 3.3, 15.0, 33.0, 9.0, 50.0, 'per piece', NULL, 1, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Pineapple (Anaros)', NULL, 50, 0.5, 13.0, 0.1, 1.4, 150.0, 'per slice', NULL, 1, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Pomegranate (Dalim)', NULL, 83, 1.7, 19.0, 1.2, 4.0, 150.0, 'per piece', NULL, 1, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Apple', NULL, 52, 0.3, 14.0, 0.2, 2.4, 180.0, 'per piece', NULL, 0, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Orange', NULL, 47, 0.9, 12.0, 0.1, 2.4, 150.0, 'per piece', NULL, 0, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Grapes', NULL, 69, 0.7, 18.0, 0.2, 0.9, 100.0, 'per serving', NULL, 0, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Strawberry', NULL, 32, 0.7, 7.7, 0.3, 2.0, 100.0, 'per serving', NULL, 0, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Blueberry', NULL, 57, 0.7, 14.0, 0.3, 2.4, 100.0, 'per serving', NULL, 0, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Dates (Khejur)', NULL, 277, 1.8, 75.0, 0.2, 6.7, 30.0, 'per piece', NULL, 1, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Avocado', NULL, 160, 2.0, 9.0, 15.0, 6.7, 100.0, 'per half', NULL, 0, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Dragon Fruit', NULL, 50, 1.1, 11.0, 0.4, 3.0, 150.0, 'per piece', NULL, 0, 'Fruit', NULL);")

        // ===== MORE BANGLADESHI FOODS =====

        // Bangladeshi Fish Varieties
        db.execSQL("$insertPrefix ('Rui Maach Bhaji', NULL, 130, 18.0, 2.0, 6.0, 0.2, 150.0, 'per serving', NULL, 1, 'Curry', NULL);")
        db.execSQL("$insertPrefix ('Pabda Maach Jhol', NULL, 110, 16.0, 3.0, 4.0, 0.2, 150.0, 'per serving', NULL, 1, 'Curry', NULL);")
        db.execSQL("$insertPrefix ('Koi Maach Curry', NULL, 125, 17.0, 3.0, 5.0, 0.2, 150.0, 'per serving', NULL, 1, 'Curry', NULL);")
        db.execSQL("$insertPrefix ('Tengra Maach Jhol', NULL, 105, 15.0, 3.0, 4.0, 0.2, 150.0, 'per serving', NULL, 1, 'Curry', NULL);")
        db.execSQL("$insertPrefix ('Magur Maach Curry', NULL, 115, 16.0, 2.0, 5.0, 0.1, 150.0, 'per serving', NULL, 1, 'Curry', NULL);")
        db.execSQL("$insertPrefix ('Shing Maach Bhuna', NULL, 135, 17.0, 3.0, 6.0, 0.2, 150.0, 'per serving', NULL, 1, 'Curry', NULL);")
        db.execSQL("$insertPrefix ('Boal Maach Curry', NULL, 140, 17.0, 3.0, 7.0, 0.1, 150.0, 'per serving', NULL, 1, 'Curry', NULL);")
        db.execSQL("$insertPrefix ('Pangash Maach Bhaji', NULL, 150, 15.0, 2.0, 9.0, 0.1, 150.0, 'per serving', NULL, 1, 'Curry', NULL);")
        db.execSQL("$insertPrefix ('Chital Maach Muitha', NULL, 155, 16.0, 5.0, 8.0, 0.3, 150.0, 'per serving', NULL, 1, 'Curry', NULL);")
        db.execSQL("$insertPrefix ('Mola Maach Bhaji', NULL, 120, 14.0, 2.0, 6.0, 0.2, 100.0, 'per serving', NULL, 1, 'Curry', NULL);")

        // More Bangladeshi Vegetables
        db.execSQL("$insertPrefix ('Dharosh Bhaji (Okra)', NULL, 55, 2.0, 7.0, 2.5, 3.2, 100.0, 'per serving', NULL, 1, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Kumra Bhaji (Pumpkin)', NULL, 50, 1.0, 8.0, 2.0, 1.0, 150.0, 'per serving', NULL, 1, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Mulo Bhaji (Radish)', NULL, 35, 1.0, 5.0, 1.5, 1.6, 100.0, 'per serving', NULL, 1, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Mishti Kumra Bhaji', NULL, 65, 1.5, 10.0, 2.5, 1.0, 150.0, 'per serving', NULL, 1, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Ucche Bhaji (Bitter Gourd)', NULL, 40, 1.0, 5.0, 2.0, 2.5, 100.0, 'per serving', NULL, 1, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Chichinga Bhaji (Snake Gourd)', NULL, 35, 1.5, 5.0, 1.5, 1.0, 100.0, 'per serving', NULL, 1, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Shojne Data (Drumstick)', NULL, 37, 2.1, 8.5, 0.2, 3.2, 100.0, 'per serving', NULL, 1, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Kochur Loti (Taro Stem)', NULL, 42, 0.8, 6.0, 2.0, 1.5, 100.0, 'per serving', NULL, 1, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Kochur Mukhi (Taro Root)', NULL, 112, 1.5, 26.0, 0.2, 4.1, 100.0, 'per serving', NULL, 1, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Palang Shak (Spinach)', NULL, 23, 2.9, 3.6, 0.4, 2.2, 100.0, 'per serving', NULL, 1, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Lal Shak (Red Spinach)', NULL, 18, 2.0, 3.0, 0.2, 2.0, 100.0, 'per serving', NULL, 1, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Kolmi Shak', NULL, 19, 2.6, 3.1, 0.2, 2.1, 100.0, 'per serving', NULL, 1, 'Vegetable', NULL);")

        // More Bangladeshi Street Food
        db.execSQL("$insertPrefix ('Dalpuri', NULL, 310, 7.0, 35.0, 16.0, 2.5, 60.0, 'per piece', NULL, 1, 'StreetFood', NULL);")
        db.execSQL("$insertPrefix ('Alu Chop', NULL, 260, 4.0, 28.0, 14.0, 2.0, 60.0, 'per piece', NULL, 1, 'StreetFood', NULL);")
        db.execSQL("$insertPrefix ('Chicken Tikka', NULL, 190, 22.0, 4.0, 10.0, 0.5, 100.0, 'per serving', NULL, 1, 'StreetFood', NULL);")
        db.execSQL("$insertPrefix ('Sheek Kebab', NULL, 220, 18.0, 6.0, 14.0, 0.5, 60.0, 'per piece', NULL, 1, 'StreetFood', NULL);")
        db.execSQL("$insertPrefix ('Boti Kebab', NULL, 215, 17.0, 5.0, 14.0, 0.3, 80.0, 'per serving', NULL, 1, 'StreetFood', NULL);")
        db.execSQL("$insertPrefix ('Reshmi Kebab', NULL, 200, 20.0, 4.0, 11.0, 0.3, 80.0, 'per serving', NULL, 1, 'StreetFood', NULL);")
        db.execSQL("$insertPrefix ('Shami Kebab', NULL, 250, 14.0, 12.0, 17.0, 1.0, 50.0, 'per piece', NULL, 1, 'StreetFood', NULL);")
        db.execSQL("$insertPrefix ('Mughlai Paratha', NULL, 330, 8.0, 36.0, 17.0, 1.5, 120.0, 'per piece', NULL, 1, 'StreetFood', NULL);")
        db.execSQL("$insertPrefix ('Haleem', NULL, 160, 10.0, 18.0, 6.0, 2.0, 200.0, 'per bowl', NULL, 1, 'StreetFood', NULL);")
        db.execSQL("$insertPrefix ('Beef Burger (Local)', NULL, 280, 15.0, 28.0, 13.0, 1.5, 200.0, 'per piece', NULL, 1, 'StreetFood', NULL);")

        // More Bangladeshi Sweets
        db.execSQL("$insertPrefix ('Gulab Jamun', NULL, 350, 4.0, 48.0, 16.0, 0.0, 40.0, 'per piece', NULL, 1, 'Sweet', NULL);")
        db.execSQL("$insertPrefix ('Rasmalai', NULL, 200, 5.0, 28.0, 8.0, 0.0, 80.0, 'per piece', NULL, 1, 'Sweet', NULL);")
        db.execSQL("$insertPrefix ('Pitha (Chitoi)', NULL, 180, 3.0, 30.0, 5.0, 0.5, 80.0, 'per piece', NULL, 1, 'Sweet', NULL);")
        db.execSQL("$insertPrefix ('Pitha (Bhapa)', NULL, 220, 4.0, 35.0, 7.0, 0.5, 80.0, 'per piece', NULL, 1, 'Sweet', NULL);")
        db.execSQL("$insertPrefix ('Pitha (Pakan)', NULL, 380, 4.0, 50.0, 18.0, 0.5, 60.0, 'per piece', NULL, 1, 'Sweet', NULL);")
        db.execSQL("$insertPrefix ('Malpua', NULL, 300, 4.0, 40.0, 14.0, 0.5, 60.0, 'per piece', NULL, 1, 'Sweet', NULL);")
        db.execSQL("$insertPrefix ('Balu Shahi', NULL, 340, 3.0, 45.0, 17.0, 0.0, 50.0, 'per piece', NULL, 1, 'Sweet', NULL);")
        db.execSQL("$insertPrefix ('Doi Bora', NULL, 120, 5.0, 14.0, 5.0, 1.0, 100.0, 'per serving', NULL, 1, 'Sweet', NULL);")
        db.execSQL("$insertPrefix ('Kheer', NULL, 135, 4.0, 19.0, 5.0, 0.2, 150.0, 'per bowl', NULL, 1, 'Sweet', NULL);")

        // ===== INDIAN FOODS =====
        db.execSQL("$insertPrefix ('Butter Chicken', NULL, 170, 14.0, 8.0, 10.0, 1.0, 200.0, 'per serving', NULL, 0, 'Indian', NULL);")
        db.execSQL("$insertPrefix ('Palak Paneer', NULL, 160, 10.0, 6.0, 11.0, 2.0, 200.0, 'per serving', NULL, 0, 'Indian', NULL);")
        db.execSQL("$insertPrefix ('Dal Makhani', NULL, 120, 6.0, 14.0, 5.0, 3.0, 200.0, 'per serving', NULL, 0, 'Indian', NULL);")
        db.execSQL("$insertPrefix ('Tandoori Chicken', NULL, 165, 25.0, 3.0, 6.0, 0.3, 150.0, 'per piece', NULL, 0, 'Indian', NULL);")
        db.execSQL("$insertPrefix ('Chicken Tikka Masala', NULL, 175, 16.0, 7.0, 10.0, 1.0, 200.0, 'per serving', NULL, 0, 'Indian', NULL);")
        db.execSQL("$insertPrefix ('Chole (Chickpea Curry)', NULL, 120, 6.0, 18.0, 3.0, 5.0, 200.0, 'per serving', NULL, 0, 'Indian', NULL);")
        db.execSQL("$insertPrefix ('Rajma (Kidney Bean Curry)', NULL, 115, 7.0, 17.0, 2.5, 5.5, 200.0, 'per serving', NULL, 0, 'Indian', NULL);")
        db.execSQL("$insertPrefix ('Aloo Paratha', NULL, 280, 5.0, 35.0, 13.0, 2.0, 80.0, 'per piece', NULL, 0, 'Indian', NULL);")
        db.execSQL("$insertPrefix ('Dosa (Plain)', NULL, 135, 3.0, 25.0, 3.0, 0.8, 120.0, 'per piece', NULL, 0, 'Indian', NULL);")
        db.execSQL("$insertPrefix ('Masala Dosa', NULL, 210, 5.0, 32.0, 7.0, 2.0, 200.0, 'per piece', NULL, 0, 'Indian', NULL);")
        db.execSQL("$insertPrefix ('Idli', NULL, 39, 2.0, 8.0, 0.1, 0.5, 40.0, 'per piece', NULL, 0, 'Indian', NULL);")
        db.execSQL("$insertPrefix ('Sambar', NULL, 55, 3.0, 8.0, 1.5, 2.5, 200.0, 'per bowl', NULL, 0, 'Indian', NULL);")
        db.execSQL("$insertPrefix ('Upma', NULL, 130, 4.0, 18.0, 5.0, 2.0, 200.0, 'per serving', NULL, 0, 'Indian', NULL);")
        db.execSQL("$insertPrefix ('Vada Pav', NULL, 290, 6.0, 35.0, 14.0, 2.0, 120.0, 'per piece', NULL, 0, 'Indian', NULL);")
        db.execSQL("$insertPrefix ('Pav Bhaji', NULL, 200, 5.0, 28.0, 8.0, 3.0, 250.0, 'per serving', NULL, 0, 'Indian', NULL);")
        db.execSQL("$insertPrefix ('Hyderabadi Biryani', NULL, 185, 10.0, 22.0, 7.0, 0.5, 300.0, 'per plate', NULL, 0, 'Indian', NULL);")
        db.execSQL("$insertPrefix ('Malai Kofta', NULL, 190, 6.0, 12.0, 14.0, 1.5, 200.0, 'per serving', NULL, 0, 'Indian', NULL);")
        db.execSQL("$insertPrefix ('Paneer Tikka', NULL, 230, 16.0, 8.0, 15.0, 1.0, 150.0, 'per serving', NULL, 0, 'Indian', NULL);")
        db.execSQL("$insertPrefix ('Gulab Jamun (Indian)', NULL, 350, 4.0, 48.0, 16.0, 0.0, 40.0, 'per piece', NULL, 0, 'Indian', NULL);")

        // ===== GYM / FITNESS STAPLES =====
        db.execSQL("$insertPrefix ('Oats (Dry)', NULL, 389, 16.9, 66.0, 6.9, 10.6, 40.0, 'per serving', NULL, 0, 'Fitness', NULL);")
        db.execSQL("$insertPrefix ('Greek Yogurt (Plain)', NULL, 59, 10.0, 3.6, 0.4, 0.0, 170.0, 'per cup', NULL, 0, 'Fitness', NULL);")
        db.execSQL("$insertPrefix ('Cottage Cheese (Low Fat)', NULL, 98, 11.0, 3.4, 4.3, 0.0, 100.0, 'per serving', NULL, 0, 'Fitness', NULL);")
        db.execSQL("$insertPrefix ('Peanut Butter', NULL, 588, 25.0, 20.0, 50.0, 6.0, 32.0, 'per 2 tbsp', NULL, 0, 'Fitness', NULL);")
        db.execSQL("$insertPrefix ('Almond Butter', NULL, 614, 21.0, 19.0, 56.0, 10.5, 32.0, 'per 2 tbsp', NULL, 0, 'Fitness', NULL);")
        db.execSQL("$insertPrefix ('Brown Rice (Cooked)', NULL, 112, 2.3, 24.0, 0.8, 1.8, 200.0, 'per bowl', NULL, 0, 'Fitness', NULL);")
        db.execSQL("$insertPrefix ('Sweet Potato (Baked)', NULL, 90, 2.0, 21.0, 0.1, 3.3, 150.0, 'per piece', NULL, 0, 'Fitness', NULL);")
        db.execSQL("$insertPrefix ('Quinoa (Cooked)', NULL, 120, 4.4, 21.0, 1.9, 2.8, 185.0, 'per cup', NULL, 0, 'Fitness', NULL);")
        db.execSQL("$insertPrefix ('Tuna (Canned in Water)', NULL, 116, 26.0, 0.0, 0.8, 0.0, 85.0, 'per can', NULL, 0, 'Fitness', NULL);")
        db.execSQL("$insertPrefix ('Salmon (Baked)', NULL, 208, 20.0, 0.0, 13.0, 0.0, 150.0, 'per fillet', NULL, 0, 'Fitness', NULL);")
        db.execSQL("$insertPrefix ('Turkey Breast (Cooked)', NULL, 135, 30.0, 0.0, 1.0, 0.0, 120.0, 'per serving', NULL, 0, 'Fitness', NULL);")
        db.execSQL("$insertPrefix ('Egg White (Cooked)', NULL, 52, 11.0, 0.7, 0.2, 0.0, 100.0, 'per 3 whites', NULL, 0, 'Fitness', NULL);")
        db.execSQL("$insertPrefix ('Casein Protein (Scoop)', NULL, 360, 70.0, 10.0, 4.0, 1.0, 33.0, 'per scoop', NULL, 0, 'Fitness', NULL);")
        db.execSQL("$insertPrefix ('Mass Gainer (Scoop)', NULL, 420, 20.0, 75.0, 5.0, 2.0, 100.0, 'per scoop', NULL, 0, 'Fitness', NULL);")
        db.execSQL("$insertPrefix ('BCAA Powder', NULL, 0, 0.0, 0.0, 0.0, 0.0, 10.0, 'per scoop', NULL, 0, 'Fitness', NULL);")
        db.execSQL("$insertPrefix ('Creatine Monohydrate', NULL, 0, 0.0, 0.0, 0.0, 0.0, 5.0, 'per scoop', NULL, 0, 'Fitness', NULL);")
        db.execSQL("$insertPrefix ('Protein Bar (Avg)', NULL, 400, 30.0, 40.0, 15.0, 5.0, 60.0, 'per bar', NULL, 0, 'Fitness', NULL);")

        // ===== NUTS & SEEDS =====
        db.execSQL("$insertPrefix ('Almonds', NULL, 579, 21.0, 22.0, 50.0, 12.5, 28.0, 'per handful', NULL, 0, 'NutsSeed', NULL);")
        db.execSQL("$insertPrefix ('Cashews', NULL, 553, 18.0, 30.0, 44.0, 3.3, 28.0, 'per handful', NULL, 0, 'NutsSeed', NULL);")
        db.execSQL("$insertPrefix ('Walnuts', NULL, 654, 15.0, 14.0, 65.0, 6.7, 28.0, 'per handful', NULL, 0, 'NutsSeed', NULL);")
        db.execSQL("$insertPrefix ('Pistachios', NULL, 560, 20.0, 28.0, 45.0, 10.6, 28.0, 'per handful', NULL, 0, 'NutsSeed', NULL);")
        db.execSQL("$insertPrefix ('Peanuts (Roasted)', NULL, 567, 26.0, 16.0, 49.0, 8.5, 28.0, 'per handful', NULL, 0, 'NutsSeed', NULL);")
        db.execSQL("$insertPrefix ('Chia Seeds', NULL, 486, 17.0, 42.0, 31.0, 34.4, 15.0, 'per tbsp', NULL, 0, 'NutsSeed', NULL);")
        db.execSQL("$insertPrefix ('Flax Seeds', NULL, 534, 18.0, 29.0, 42.0, 27.3, 15.0, 'per tbsp', NULL, 0, 'NutsSeed', NULL);")
        db.execSQL("$insertPrefix ('Sunflower Seeds', NULL, 584, 21.0, 20.0, 51.0, 8.6, 28.0, 'per handful', NULL, 0, 'NutsSeed', NULL);")
        db.execSQL("$insertPrefix ('Pumpkin Seeds', NULL, 559, 30.0, 11.0, 49.0, 6.0, 28.0, 'per handful', NULL, 0, 'NutsSeed', NULL);")
        db.execSQL("$insertPrefix ('Mixed Nuts', NULL, 595, 20.0, 21.0, 52.0, 7.0, 28.0, 'per handful', NULL, 0, 'NutsSeed', NULL);")

        // ===== INTERNATIONAL FAST FOOD =====
        db.execSQL("$insertPrefix ('Chicken Nuggets (6pc)', NULL, 270, 13.0, 16.0, 17.0, 1.0, 95.0, 'per 6 pieces', NULL, 0, 'FastFood', NULL);")
        db.execSQL("$insertPrefix ('French Fries (Medium)', NULL, 320, 4.0, 42.0, 15.0, 3.8, 117.0, 'per serving', NULL, 0, 'FastFood', NULL);")
        db.execSQL("$insertPrefix ('Big Mac', NULL, 257, 13.0, 19.0, 14.0, 1.5, 200.0, 'per piece', NULL, 0, 'FastFood', NULL);")
        db.execSQL("$insertPrefix ('Cheese Pizza (1 slice)', NULL, 266, 11.0, 33.0, 10.0, 2.0, 107.0, 'per slice', NULL, 0, 'FastFood', NULL);")
        db.execSQL("$insertPrefix ('Pepperoni Pizza (1 slice)', NULL, 298, 12.0, 31.0, 14.0, 2.0, 107.0, 'per slice', NULL, 0, 'FastFood', NULL);")
        db.execSQL("$insertPrefix ('Fried Chicken (1pc)', NULL, 260, 18.0, 12.0, 16.0, 0.5, 120.0, 'per piece', NULL, 0, 'FastFood', NULL);")
        db.execSQL("$insertPrefix ('Chicken Sandwich', NULL, 420, 22.0, 40.0, 19.0, 2.0, 200.0, 'per piece', NULL, 0, 'FastFood', NULL);")
        db.execSQL("$insertPrefix ('Fish Fillet Sandwich', NULL, 380, 15.0, 38.0, 19.0, 1.5, 170.0, 'per piece', NULL, 0, 'FastFood', NULL);")
        db.execSQL("$insertPrefix ('Chicken Wrap', NULL, 340, 18.0, 35.0, 14.0, 2.0, 200.0, 'per piece', NULL, 0, 'FastFood', NULL);")
        db.execSQL("$insertPrefix ('Shawarma (Chicken)', NULL, 250, 16.0, 22.0, 11.0, 1.5, 200.0, 'per piece', NULL, 0, 'FastFood', NULL);")
        db.execSQL("$insertPrefix ('Doner Kebab', NULL, 235, 14.0, 20.0, 11.0, 1.5, 200.0, 'per serving', NULL, 0, 'FastFood', NULL);")
        db.execSQL("$insertPrefix ('Subway 6-inch Turkey', NULL, 250, 18.0, 40.0, 3.5, 5.0, 220.0, 'per piece', NULL, 0, 'FastFood', NULL);")
        db.execSQL("$insertPrefix ('Hot Dog', NULL, 290, 10.0, 24.0, 17.0, 1.0, 100.0, 'per piece', NULL, 0, 'FastFood', NULL);")

        // ===== PASTA & NOODLES =====
        db.execSQL("$insertPrefix ('Pasta (Cooked)', NULL, 158, 6.0, 31.0, 0.9, 1.8, 200.0, 'per bowl', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Pasta (Dry)', NULL, 350, 12.0, 70.0, 1.5, 3.0, 100.0, 'per serving', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Pasta Carbonara', NULL, 250, 10.0, 25.0, 15.0, 1.0, 350.0, 'per plate', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Pasta Alfredo (Chicken)', NULL, 220, 12.0, 20.0, 12.0, 1.0, 350.0, 'per plate', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Pasta Marinara', NULL, 140, 5.0, 25.0, 4.0, 2.5, 300.0, 'per plate', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Pasta Pesto', NULL, 210, 6.0, 24.0, 10.0, 2.0, 250.0, 'per plate', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Whole Wheat Pasta', NULL, 124, 5.3, 26.5, 0.5, 4.5, 200.0, 'per bowl', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Fusilli Pasta', NULL, 158, 6.0, 31.0, 0.9, 1.8, 200.0, 'per bowl', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Penne Pasta', NULL, 158, 6.0, 31.0, 0.9, 1.8, 200.0, 'per bowl', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Farfalle (Bow Tie) Pasta', NULL, 158, 6.0, 31.0, 0.9, 1.8, 200.0, 'per bowl', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Spaghetti (Cooked)', NULL, 158, 6.0, 31.0, 0.9, 1.8, 200.0, 'per plate', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Spaghetti Bolognese', NULL, 175, 10.0, 22.0, 6.0, 2.0, 300.0, 'per plate', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Penne Arrabiata', NULL, 160, 5.0, 28.0, 3.0, 2.5, 300.0, 'per plate', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Mac and Cheese', NULL, 200, 8.0, 24.0, 8.0, 1.0, 250.0, 'per serving', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Lasagna', NULL, 175, 10.0, 17.0, 8.0, 1.5, 250.0, 'per serving', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Ravioli (Cheese)', NULL, 200, 8.0, 28.0, 6.0, 1.5, 200.0, 'per serving', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Fettuccine (Cooked)', NULL, 158, 6.0, 31.0, 0.9, 1.8, 200.0, 'per bowl', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Gnocchi (Cooked)', NULL, 131, 3.3, 28.0, 0.1, 2.0, 200.0, 'per bowl', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Instant Noodles', NULL, 440, 9.0, 61.0, 18.0, 2.0, 85.0, 'per packet', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Noodles (Boiled)', NULL, 138, 4.5, 25.0, 2.1, 1.2, 200.0, 'per bowl', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Ramen (Tonkotsu)', NULL, 180, 12.0, 18.0, 7.0, 1.0, 350.0, 'per bowl', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Pad Thai', NULL, 155, 8.0, 22.0, 4.0, 1.5, 300.0, 'per plate', NULL, 0, 'Pasta', NULL);")
        db.execSQL("$insertPrefix ('Chow Mein', NULL, 160, 8.0, 20.0, 6.0, 2.0, 250.0, 'per plate', NULL, 0, 'Pasta', NULL);")

        // ===== MORE INTERNATIONAL FOODS =====
        db.execSQL("$insertPrefix ('Beef Steak (Ribeye)', NULL, 291, 24.0, 0.0, 22.0, 0.0, 200.0, 'per piece', NULL, 0, 'Meat', NULL);")
        db.execSQL("$insertPrefix ('Burrito (Bean & Cheese)', NULL, 206, 8.0, 28.0, 7.0, 5.0, 200.0, 'per piece', NULL, 0, 'Mexican', NULL);")
        db.execSQL("$insertPrefix ('Tacos (Beef)', NULL, 226, 12.0, 20.0, 11.0, 2.0, 100.0, 'per taco', NULL, 0, 'Mexican', NULL);")
        db.execSQL("$insertPrefix ('Sushi (Nigiri Salmon)', NULL, 140, 6.0, 24.0, 2.0, 0.0, 50.0, 'per piece', NULL, 0, 'Japanese', NULL);")
        db.execSQL("$insertPrefix ('Burger (Cheese)', NULL, 250, 13.0, 30.0, 9.0, 1.5, 150.0, 'per piece', NULL, 0, 'FastFood', NULL);")
        db.execSQL("$insertPrefix ('Sandwich (Club)', NULL, 210, 12.0, 22.0, 8.0, 2.0, 250.0, 'per piece', NULL, 0, 'FastFood', NULL);")
        db.execSQL("$insertPrefix ('Salad (Chicken)', NULL, 120, 15.0, 4.0, 5.0, 2.0, 200.0, 'per bowl', NULL, 0, 'Salad', NULL);")
        db.execSQL("$insertPrefix ('Soup (Tomato)', NULL, 30, 1.0, 5.0, 0.5, 1.0, 250.0, 'per bowl', NULL, 0, 'Soup', NULL);")
        db.execSQL("$insertPrefix ('Soup (Chicken Noodle)', NULL, 45, 4.0, 5.0, 1.5, 0.5, 250.0, 'per bowl', NULL, 0, 'Soup', NULL);")
        db.execSQL("$insertPrefix ('Soup (Lentil)', NULL, 56, 3.5, 8.0, 0.8, 3.0, 250.0, 'per bowl', NULL, 0, 'Soup', NULL);")
        db.execSQL("$insertPrefix ('Yogurt (Greek Mixed Berries)', NULL, 90, 8.0, 12.0, 0.4, 1.0, 150.0, 'per cup', NULL, 0, 'Dairy', NULL);")
        db.execSQL("$insertPrefix ('Protein Shake (Whey)', NULL, 110, 24.0, 3.0, 1.0, 0.0, 300.0, 'per shake', NULL, 0, 'Fitness', NULL);")

        // ===== BREAKFAST ITEMS =====
        db.execSQL("$insertPrefix ('Cornflakes', NULL, 357, 8.0, 84.0, 0.7, 3.0, 30.0, 'per cup', NULL, 0, 'Breakfast', NULL);")
        db.execSQL("$insertPrefix ('Muesli', NULL, 370, 10.0, 68.0, 7.0, 7.5, 50.0, 'per serving', NULL, 0, 'Breakfast', NULL);")
        db.execSQL("$insertPrefix ('Granola', NULL, 471, 10.0, 64.0, 20.0, 7.0, 50.0, 'per serving', NULL, 0, 'Breakfast', NULL);")
        db.execSQL("$insertPrefix ('Pancake (Plain)', NULL, 227, 6.0, 33.0, 8.0, 1.0, 80.0, 'per piece', NULL, 0, 'Breakfast', NULL);")
        db.execSQL("$insertPrefix ('Waffle', NULL, 291, 8.0, 33.0, 14.0, 1.0, 75.0, 'per piece', NULL, 0, 'Breakfast', NULL);")
        db.execSQL("$insertPrefix ('French Toast', NULL, 229, 8.0, 22.0, 12.0, 0.8, 80.0, 'per piece', NULL, 0, 'Breakfast', NULL);")
        db.execSQL("$insertPrefix ('Scrambled Eggs', NULL, 148, 10.0, 1.6, 11.0, 0.0, 100.0, 'per serving', NULL, 0, 'Breakfast', NULL);")
        db.execSQL("$insertPrefix ('Bacon (3 strips)', NULL, 541, 37.0, 1.4, 42.0, 0.0, 30.0, 'per 3 strips', NULL, 0, 'Breakfast', NULL);")
        db.execSQL("$insertPrefix ('Toast (White)', NULL, 265, 9.0, 49.0, 3.2, 2.7, 30.0, 'per slice', NULL, 0, 'Breakfast', NULL);")
        db.execSQL("$insertPrefix ('Toast (Whole Wheat)', NULL, 247, 13.0, 43.0, 3.4, 7.0, 30.0, 'per slice', NULL, 0, 'Breakfast', NULL);")
        db.execSQL("$insertPrefix ('Yogurt (Flavored)', NULL, 100, 4.0, 17.0, 1.5, 0.0, 150.0, 'per cup', NULL, 0, 'Breakfast', NULL);")
        db.execSQL("$insertPrefix ('Overnight Oats', NULL, 180, 7.0, 30.0, 4.0, 4.0, 250.0, 'per jar', NULL, 0, 'Breakfast', NULL);")
        db.execSQL("$insertPrefix ('Smoothie Bowl (Acai)', NULL, 210, 5.0, 35.0, 6.0, 5.0, 300.0, 'per bowl', NULL, 0, 'Breakfast', NULL);")

        // ===== SALADS & HEALTHY =====
        db.execSQL("$insertPrefix ('Caesar Salad', NULL, 130, 5.0, 8.0, 9.0, 2.0, 200.0, 'per serving', NULL, 0, 'Salad', NULL);")
        db.execSQL("$insertPrefix ('Greek Salad', NULL, 95, 4.0, 6.0, 7.0, 2.0, 200.0, 'per serving', NULL, 0, 'Salad', NULL);")
        db.execSQL("$insertPrefix ('Garden Salad', NULL, 35, 2.0, 6.0, 0.5, 2.5, 150.0, 'per serving', NULL, 0, 'Salad', NULL);")
        db.execSQL("$insertPrefix ('Chicken Caesar Salad', NULL, 165, 14.0, 8.0, 9.0, 2.0, 250.0, 'per serving', NULL, 0, 'Salad', NULL);")
        db.execSQL("$insertPrefix ('Tuna Salad', NULL, 180, 16.0, 5.0, 11.0, 1.0, 200.0, 'per serving', NULL, 0, 'Salad', NULL);")
        db.execSQL("$insertPrefix ('Hummus', NULL, 166, 8.0, 14.0, 10.0, 6.0, 100.0, 'per serving', NULL, 0, 'Salad', NULL);")

        // ===== BEVERAGES =====
        db.execSQL("$insertPrefix ('Black Coffee', NULL, 1, 0.1, 0.0, 0.0, 0.0, 240.0, 'per cup', NULL, 0, 'Beverage', NULL);")
        db.execSQL("$insertPrefix ('Latte', NULL, 60, 3.0, 5.0, 3.0, 0.0, 240.0, 'per cup', NULL, 0, 'Beverage', NULL);")
        db.execSQL("$insertPrefix ('Cappuccino', NULL, 55, 3.0, 5.0, 2.5, 0.0, 240.0, 'per cup', NULL, 0, 'Beverage', NULL);")
        db.execSQL("$insertPrefix ('Green Tea', NULL, 1, 0.0, 0.0, 0.0, 0.0, 240.0, 'per cup', NULL, 0, 'Beverage', NULL);")
        db.execSQL("$insertPrefix ('Coca-Cola', NULL, 42, 0.0, 11.0, 0.0, 0.0, 330.0, 'per can', NULL, 0, 'Beverage', NULL);")
        db.execSQL("$insertPrefix ('Orange Juice', NULL, 45, 0.7, 10.0, 0.2, 0.2, 250.0, 'per glass', NULL, 0, 'Beverage', NULL);")
        db.execSQL("$insertPrefix ('Coconut Water', NULL, 19, 0.7, 3.7, 0.2, 1.1, 330.0, 'per pack', NULL, 0, 'Beverage', NULL);")
        db.execSQL("$insertPrefix ('Milk (Full Fat)', NULL, 61, 3.2, 4.8, 3.3, 0.0, 250.0, 'per glass', NULL, 0, 'Beverage', NULL);")
        db.execSQL("$insertPrefix ('Milk (Skim)', NULL, 34, 3.4, 5.0, 0.1, 0.0, 250.0, 'per glass', NULL, 0, 'Beverage', NULL);")
        db.execSQL("$insertPrefix ('Almond Milk', NULL, 17, 0.6, 1.5, 1.1, 0.2, 250.0, 'per glass', NULL, 0, 'Beverage', NULL);")
        db.execSQL("$insertPrefix ('Protein Shake (Mixed)', NULL, 130, 25.0, 5.0, 2.0, 0.5, 350.0, 'per shake', NULL, 0, 'Beverage', NULL);")
        db.execSQL("$insertPrefix ('Energy Drink', NULL, 45, 0.0, 11.0, 0.0, 0.0, 250.0, 'per can', NULL, 0, 'Beverage', NULL);")
        db.execSQL("$insertPrefix ('Isotonic Sports Drink', NULL, 26, 0.0, 6.0, 0.0, 0.0, 500.0, 'per bottle', NULL, 0, 'Beverage', NULL);")

        // ===== CHINESE & EAST ASIAN =====
        db.execSQL("$insertPrefix ('Fried Rice (Chinese)', NULL, 165, 5.0, 23.0, 6.0, 1.0, 250.0, 'per plate', NULL, 0, 'Chinese', NULL);")
        db.execSQL("$insertPrefix ('Chicken Fried Rice', NULL, 175, 9.0, 22.0, 6.0, 1.0, 250.0, 'per plate', NULL, 0, 'Chinese', NULL);")
        db.execSQL("$insertPrefix ('Spring Roll (Fried)', NULL, 200, 4.0, 22.0, 11.0, 1.5, 50.0, 'per piece', NULL, 0, 'Chinese', NULL);")
        db.execSQL("$insertPrefix ('Dim Sum (Steamed)', NULL, 180, 8.0, 18.0, 8.0, 1.0, 50.0, 'per piece', NULL, 0, 'Chinese', NULL);")
        db.execSQL("$insertPrefix ('Sweet and Sour Chicken', NULL, 165, 12.0, 18.0, 5.0, 1.0, 200.0, 'per serving', NULL, 0, 'Chinese', NULL);")
        db.execSQL("$insertPrefix ('Kung Pao Chicken', NULL, 175, 14.0, 12.0, 9.0, 1.5, 200.0, 'per serving', NULL, 0, 'Chinese', NULL);")
        db.execSQL("$insertPrefix ('Wonton Soup', NULL, 70, 5.0, 8.0, 2.0, 0.5, 250.0, 'per bowl', NULL, 0, 'Chinese', NULL);")
        db.execSQL("$insertPrefix ('Sushi Roll (Avg)', NULL, 145, 5.0, 25.0, 3.0, 1.0, 100.0, 'per roll', NULL, 0, 'Chinese', NULL);")
        db.execSQL("$insertPrefix ('Sashimi (Salmon)', NULL, 127, 21.0, 0.0, 5.0, 0.0, 100.0, 'per serving', NULL, 0, 'Chinese', NULL);")
        db.execSQL("$insertPrefix ('Miso Soup', NULL, 40, 3.0, 5.0, 1.0, 1.0, 250.0, 'per bowl', NULL, 0, 'Chinese', NULL);")

        // ===== MIDDLE EASTERN =====
        db.execSQL("$insertPrefix ('Falafel', NULL, 333, 13.0, 32.0, 18.0, 5.0, 40.0, 'per piece', NULL, 0, 'MiddleEast', NULL);")
        db.execSQL("$insertPrefix ('Kebab (Beef)', NULL, 210, 17.0, 3.0, 14.0, 0.3, 100.0, 'per serving', NULL, 0, 'MiddleEast', NULL);")
        db.execSQL("$insertPrefix ('Tabbouleh', NULL, 85, 2.0, 11.0, 4.0, 2.5, 150.0, 'per serving', NULL, 0, 'MiddleEast', NULL);")
        db.execSQL("$insertPrefix ('Baba Ganoush', NULL, 100, 2.0, 8.0, 7.0, 2.5, 100.0, 'per serving', NULL, 0, 'MiddleEast', NULL);")
        db.execSQL("$insertPrefix ('Pita Bread', NULL, 275, 9.0, 56.0, 1.2, 2.0, 60.0, 'per piece', NULL, 0, 'MiddleEast', NULL);")
        db.execSQL("$insertPrefix ('Fattoush', NULL, 90, 2.0, 10.0, 5.0, 2.0, 150.0, 'per serving', NULL, 0, 'MiddleEast', NULL);")
        db.execSQL("$insertPrefix ('Lamb Shawarma', NULL, 270, 17.0, 22.0, 13.0, 1.5, 200.0, 'per piece', NULL, 0, 'MiddleEast', NULL);")

        // ===== CONDIMENTS & SAUCES =====
        db.execSQL("$insertPrefix ('Ketchup', NULL, 112, 1.0, 28.0, 0.1, 0.3, 15.0, 'per tbsp', NULL, 0, 'Condiment', NULL);")
        db.execSQL("$insertPrefix ('Mayonnaise', NULL, 680, 1.0, 1.0, 75.0, 0.0, 15.0, 'per tbsp', NULL, 0, 'Condiment', NULL);")
        db.execSQL("$insertPrefix ('Soy Sauce', NULL, 53, 8.0, 5.0, 0.1, 0.0, 15.0, 'per tbsp', NULL, 0, 'Condiment', NULL);")
        db.execSQL("$insertPrefix ('Honey', NULL, 304, 0.3, 82.0, 0.0, 0.2, 21.0, 'per tbsp', NULL, 0, 'Condiment', NULL);")
        db.execSQL("$insertPrefix ('Olive Oil', NULL, 884, 0.0, 0.0, 100.0, 0.0, 14.0, 'per tbsp', NULL, 0, 'Condiment', NULL);")
        db.execSQL("$insertPrefix ('Butter', NULL, 717, 0.9, 0.1, 81.0, 0.0, 14.0, 'per tbsp', NULL, 0, 'Condiment', NULL);")
        db.execSQL("$insertPrefix ('Ghee', NULL, 900, 0.0, 0.0, 100.0, 0.0, 14.0, 'per tbsp', NULL, 0, 'Condiment', NULL);")
        db.execSQL("$insertPrefix ('Mustard Sauce', NULL, 66, 4.4, 5.3, 3.3, 3.3, 5.0, 'per tsp', NULL, 0, 'Condiment', NULL);")
        db.execSQL("$insertPrefix ('Hot Sauce', NULL, 11, 0.3, 2.0, 0.1, 0.5, 5.0, 'per tsp', NULL, 0, 'Condiment', NULL);")
        db.execSQL("$insertPrefix ('Chili Sauce', NULL, 87, 1.0, 20.0, 0.3, 1.0, 15.0, 'per tbsp', NULL, 0, 'Condiment', NULL);")
        db.execSQL("$insertPrefix ('BBQ Sauce', NULL, 172, 0.8, 40.0, 0.6, 0.5, 30.0, 'per tbsp', NULL, 0, 'Condiment', NULL);")

        // ===== SNACKS =====
        db.execSQL("$insertPrefix ('Potato Chips', NULL, 536, 7.0, 53.0, 35.0, 4.8, 28.0, 'per serving', NULL, 0, 'Snack', NULL);")
        db.execSQL("$insertPrefix ('Popcorn (Plain)', NULL, 375, 11.0, 74.0, 4.3, 15.0, 30.0, 'per cup', NULL, 0, 'Snack', NULL);")
        db.execSQL("$insertPrefix ('Dark Chocolate (70%)', NULL, 598, 8.0, 46.0, 43.0, 11.0, 30.0, 'per square', NULL, 0, 'Snack', NULL);")
        db.execSQL("$insertPrefix ('Milk Chocolate', NULL, 535, 8.0, 60.0, 30.0, 3.4, 30.0, 'per square', NULL, 0, 'Snack', NULL);")
        db.execSQL("$insertPrefix ('Rice Cake', NULL, 387, 8.0, 82.0, 2.8, 1.2, 9.0, 'per cake', NULL, 0, 'Snack', NULL);")
        db.execSQL("$insertPrefix ('Trail Mix', NULL, 462, 14.0, 44.0, 29.0, 5.0, 40.0, 'per serving', NULL, 0, 'Snack', NULL);")
        db.execSQL("$insertPrefix ('Granola Bar', NULL, 410, 6.0, 64.0, 15.0, 3.5, 40.0, 'per bar', NULL, 0, 'Snack', NULL);")
        db.execSQL("$insertPrefix ('Beef Jerky', NULL, 410, 33.0, 11.0, 26.0, 1.8, 28.0, 'per serving', NULL, 0, 'Snack', NULL);")
        db.execSQL("$insertPrefix ('Crackers', NULL, 484, 10.0, 62.0, 22.0, 2.5, 30.0, 'per serving', NULL, 0, 'Snack', NULL);")

        // ===== FROZEN & DESSERTS =====
        db.execSQL("$insertPrefix ('Vanilla Ice Cream', NULL, 207, 3.5, 24.0, 11.0, 0.7, 100.0, 'per scoop', NULL, 0, 'Dessert', NULL);")
        db.execSQL("$insertPrefix ('Chocolate Ice Cream', NULL, 216, 3.8, 28.0, 11.0, 1.6, 100.0, 'per scoop', NULL, 0, 'Dessert', NULL);")
        db.execSQL("$insertPrefix ('Frozen Yogurt', NULL, 159, 4.0, 24.0, 5.0, 0.0, 100.0, 'per scoop', NULL, 0, 'Dessert', NULL);")
        db.execSQL("$insertPrefix ('Brownie', NULL, 405, 5.0, 50.0, 21.0, 2.5, 60.0, 'per piece', NULL, 0, 'Dessert', NULL);")
        db.execSQL("$insertPrefix ('Cheesecake (1 slice)', NULL, 321, 6.0, 26.0, 22.0, 0.3, 125.0, 'per slice', NULL, 0, 'Dessert', NULL);")
        db.execSQL("$insertPrefix ('Chocolate Cake (1 slice)', NULL, 367, 5.0, 51.0, 17.0, 2.0, 100.0, 'per slice', NULL, 0, 'Dessert', NULL);")
        db.execSQL("$insertPrefix ('Tiramisu', NULL, 280, 5.0, 30.0, 15.0, 0.5, 100.0, 'per serving', NULL, 0, 'Dessert', NULL);")

        // ===== GRAINS & LEGUMES =====
        db.execSQL("$insertPrefix ('Lentils (Cooked)', NULL, 116, 9.0, 20.0, 0.4, 7.9, 200.0, 'per cup', NULL, 0, 'Grain', NULL);")
        db.execSQL("$insertPrefix ('Chickpeas (Cooked)', NULL, 164, 9.0, 27.0, 2.6, 7.6, 150.0, 'per cup', NULL, 0, 'Grain', NULL);")
        db.execSQL("$insertPrefix ('Black Beans (Cooked)', NULL, 132, 9.0, 24.0, 0.5, 8.7, 150.0, 'per cup', NULL, 0, 'Grain', NULL);")
        db.execSQL("$insertPrefix ('Kidney Beans (Cooked)', NULL, 127, 9.0, 23.0, 0.5, 7.4, 150.0, 'per cup', NULL, 0, 'Grain', NULL);")
        db.execSQL("$insertPrefix ('Tofu (Firm)', NULL, 144, 17.0, 3.0, 9.0, 2.0, 100.0, 'per serving', NULL, 0, 'Grain', NULL);")
        db.execSQL("$insertPrefix ('Tempeh', NULL, 192, 20.0, 8.0, 11.0, 5.0, 100.0, 'per serving', NULL, 0, 'Grain', NULL);")
        db.execSQL("$insertPrefix ('Couscous (Cooked)', NULL, 112, 3.8, 23.0, 0.2, 1.4, 200.0, 'per cup', NULL, 0, 'Grain', NULL);")
        db.execSQL("$insertPrefix ('Bulgur Wheat (Cooked)', NULL, 83, 3.1, 19.0, 0.2, 4.5, 182.0, 'per cup', NULL, 0, 'Grain', NULL);")
        db.execSQL("$insertPrefix ('Barley (Cooked)', NULL, 123, 2.3, 28.0, 0.4, 3.8, 157.0, 'per cup', NULL, 0, 'Grain', NULL);")

        // ===== MORE PROTEIN SOURCES =====
        db.execSQL("$insertPrefix ('Shrimp (Cooked)', NULL, 99, 24.0, 0.2, 0.3, 0.0, 100.0, 'per serving', NULL, 0, 'Protein', NULL);")
        db.execSQL("$insertPrefix ('Lamb (Cooked)', NULL, 258, 25.0, 0.0, 17.0, 0.0, 120.0, 'per serving', NULL, 0, 'Protein', NULL);")
        db.execSQL("$insertPrefix ('Pork Chop (Grilled)', NULL, 231, 26.0, 0.0, 14.0, 0.0, 120.0, 'per piece', NULL, 0, 'Protein', NULL);")
        db.execSQL("$insertPrefix ('Duck Breast', NULL, 201, 19.0, 0.0, 14.0, 0.0, 120.0, 'per piece', NULL, 0, 'Protein', NULL);")
        db.execSQL("$insertPrefix ('Cod (Baked)', NULL, 105, 23.0, 0.0, 1.0, 0.0, 120.0, 'per fillet', NULL, 0, 'Protein', NULL);")
        db.execSQL("$insertPrefix ('Sardines (Canned)', NULL, 208, 25.0, 0.0, 11.0, 0.0, 85.0, 'per can', NULL, 0, 'Protein', NULL);")
        db.execSQL("$insertPrefix ('Crab Meat', NULL, 83, 18.0, 0.0, 1.0, 0.0, 100.0, 'per serving', NULL, 0, 'Protein', NULL);")
        db.execSQL("$insertPrefix ('Ground Beef (80/20)', NULL, 254, 17.0, 0.0, 20.0, 0.0, 113.0, 'per patty', NULL, 0, 'Protein', NULL);")
        db.execSQL("$insertPrefix ('Bison (Lean)', NULL, 143, 28.0, 0.0, 2.4, 0.0, 120.0, 'per serving', NULL, 0, 'Protein', NULL);")

        // ===== DAIRY =====
        db.execSQL("$insertPrefix ('Cheddar Cheese', NULL, 403, 25.0, 1.3, 33.0, 0.0, 28.0, 'per slice', NULL, 0, 'Dairy', NULL);")
        db.execSQL("$insertPrefix ('Mozzarella Cheese', NULL, 280, 28.0, 3.1, 17.0, 0.0, 28.0, 'per slice', NULL, 0, 'Dairy', NULL);")
        db.execSQL("$insertPrefix ('Cream Cheese', NULL, 342, 6.0, 4.0, 34.0, 0.0, 28.0, 'per serving', NULL, 0, 'Dairy', NULL);")
        db.execSQL("$insertPrefix ('Parmesan', NULL, 431, 38.0, 4.0, 29.0, 0.0, 10.0, 'per tbsp', NULL, 0, 'Dairy', NULL);")
        db.execSQL("$insertPrefix ('Sour Cream', NULL, 193, 2.4, 4.6, 19.0, 0.0, 30.0, 'per tbsp', NULL, 0, 'Dairy', NULL);")
        db.execSQL("$insertPrefix ('Whipped Cream', NULL, 257, 3.0, 13.0, 22.0, 0.0, 15.0, 'per tbsp', NULL, 0, 'Dairy', NULL);")

        // ===== NEW FOODS ADDED =====
        // Breakfast
        db.execSQL("$insertPrefix ('Avocado Toast', NULL, 250, 6.0, 20.0, 16.0, 6.0, 150.0, 'per serving', NULL, 0, 'Breakfast', NULL);")
        db.execSQL("$insertPrefix ('Bagel with Cream Cheese', NULL, 350, 10.0, 45.0, 12.0, 2.0, 150.0, 'per serving', NULL, 0, 'Breakfast', NULL);")
        db.execSQL("$insertPrefix ('Eggs Benedict', NULL, 400, 17.0, 20.0, 28.0, 1.0, 200.0, 'per serving', NULL, 0, 'Breakfast', NULL);")
        db.execSQL("$insertPrefix ('Hash Browns', NULL, 220, 2.0, 25.0, 12.0, 2.0, 100.0, 'per serving', NULL, 0, 'Breakfast', NULL);")
        db.execSQL("$insertPrefix ('Blueberry Muffins', NULL, 380, 5.0, 52.0, 18.0, 1.5, 120.0, 'per muffin', NULL, 0, 'Breakfast', NULL);")
        db.execSQL("$insertPrefix ('Breakfast Burrito', NULL, 450, 20.0, 35.0, 25.0, 3.0, 250.0, 'per piece', NULL, 0, 'Breakfast', NULL);")
        db.execSQL("$insertPrefix ('Oatmeal with Berries', NULL, 210, 6.0, 38.0, 4.0, 6.0, 200.0, 'per bowl', NULL, 0, 'Breakfast', NULL);")
        db.execSQL("$insertPrefix ('Cereal with Milk', NULL, 200, 8.0, 30.0, 5.0, 2.0, 200.0, 'per bowl', NULL, 0, 'Breakfast', NULL);")
        db.execSQL("$insertPrefix ('Croissant', NULL, 231, 4.0, 26.0, 12.0, 1.0, 60.0, 'per piece', NULL, 0, 'Breakfast', NULL);")
        db.execSQL("$insertPrefix ('English Muffin', NULL, 134, 4.0, 26.0, 1.0, 1.5, 60.0, 'per piece', NULL, 0, 'Breakfast', NULL);")

        // Meats
        db.execSQL("$insertPrefix ('Pork Ribs (BBQ)', NULL, 350, 20.0, 5.0, 28.0, 0.0, 150.0, 'per serving', NULL, 0, 'Meat', NULL);")
        db.execSQL("$insertPrefix ('Roast Beef', NULL, 160, 25.0, 0.0, 6.0, 0.0, 100.0, 'per serving', NULL, 0, 'Meat', NULL);")
        db.execSQL("$insertPrefix ('Chicken Wings (Fried)', NULL, 290, 15.0, 5.0, 22.0, 0.0, 100.0, 'per serving', NULL, 0, 'Meat', NULL);")
        db.execSQL("$insertPrefix ('Veal Cutlet', NULL, 210, 25.0, 0.0, 11.0, 0.0, 100.0, 'per piece', NULL, 0, 'Meat', NULL);")
        db.execSQL("$insertPrefix ('Turkey Meatballs', NULL, 200, 18.0, 8.0, 10.0, 1.0, 120.0, 'per serving', NULL, 0, 'Meat', NULL);")
        db.execSQL("$insertPrefix ('Bratwurst Sausage', NULL, 300, 12.0, 2.0, 26.0, 0.0, 100.0, 'per link', NULL, 0, 'Meat', NULL);")
        db.execSQL("$insertPrefix ('Venison (Deer Meat)', NULL, 158, 30.0, 0.0, 3.2, 0.0, 100.0, 'per serving', NULL, 0, 'Meat', NULL);")
        db.execSQL("$insertPrefix ('Corned Beef', NULL, 250, 18.0, 0.0, 19.0, 0.0, 100.0, 'per serving', NULL, 0, 'Meat', NULL);")
        db.execSQL("$insertPrefix ('Prosciutto', NULL, 200, 28.0, 0.0, 9.0, 0.0, 50.0, 'per serving', NULL, 0, 'Meat', NULL);")
        db.execSQL("$insertPrefix ('Salami', NULL, 360, 22.0, 1.0, 30.0, 0.0, 50.0, 'per serving', NULL, 0, 'Meat', NULL);")
        db.execSQL("$insertPrefix ('Chorizo', NULL, 455, 24.0, 2.0, 38.0, 0.0, 50.0, 'per serving', NULL, 0, 'Meat', NULL);")

        // Fruits
        db.execSQL("$insertPrefix ('Kiwi', NULL, 61, 1.1, 15.0, 0.5, 3.0, 75.0, 'per piece', NULL, 0, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Plum', NULL, 46, 0.7, 11.0, 0.3, 1.4, 65.0, 'per piece', NULL, 0, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Peach', NULL, 39, 0.9, 10.0, 0.3, 1.5, 150.0, 'per piece', NULL, 0, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Pear', NULL, 57, 0.4, 15.0, 0.1, 3.1, 180.0, 'per piece', NULL, 0, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Grapefruit', NULL, 42, 0.8, 11.0, 0.1, 1.6, 200.0, 'per half', NULL, 0, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Cantaloupe', NULL, 34, 0.8, 8.0, 0.2, 0.9, 150.0, 'per slice', NULL, 0, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Honeydew Melon', NULL, 36, 0.5, 9.0, 0.1, 0.8, 150.0, 'per slice', NULL, 0, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Raspberries', NULL, 52, 1.2, 12.0, 0.7, 6.5, 100.0, 'per serving', NULL, 0, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Blackberries', NULL, 43, 1.4, 10.0, 0.5, 5.3, 100.0, 'per serving', NULL, 0, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Fig (Fresh)', NULL, 74, 0.8, 19.0, 0.3, 2.9, 50.0, 'per piece', NULL, 0, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Passion Fruit', NULL, 97, 2.2, 23.0, 0.7, 10.0, 18.0, 'per piece', NULL, 0, 'Fruit', NULL);")
        db.execSQL("$insertPrefix ('Cherry', NULL, 50, 1.0, 12.0, 0.3, 1.6, 100.0, 'per serving', NULL, 0, 'Fruit', NULL);")

        // Veggies
        db.execSQL("$insertPrefix ('Broccoli (Steamed)', NULL, 35, 2.4, 7.0, 0.4, 3.3, 100.0, 'per serving', NULL, 0, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Cauliflower (Roasted)', NULL, 40, 2.0, 5.0, 1.5, 2.5, 100.0, 'per serving', NULL, 0, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Asparagus (Grilled)', NULL, 25, 2.5, 4.5, 0.2, 2.0, 100.0, 'per serving', NULL, 0, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Brussel Sprouts', NULL, 43, 3.4, 9.0, 0.3, 3.8, 100.0, 'per serving', NULL, 0, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Zucchini (Sautéed)', NULL, 20, 1.5, 4.0, 0.4, 1.5, 100.0, 'per serving', NULL, 0, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Eggplant (Roasted)', NULL, 35, 1.0, 8.0, 0.2, 2.5, 100.0, 'per serving', NULL, 0, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Bell Peppers (Raw)', NULL, 20, 1.0, 4.5, 0.1, 1.5, 100.0, 'per serving', NULL, 0, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Carrots (Raw)', NULL, 41, 0.9, 10.0, 0.2, 2.8, 100.0, 'per serving', NULL, 0, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Celery (Raw)', NULL, 14, 0.7, 3.0, 0.2, 1.6, 100.0, 'per serving', NULL, 0, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Cucumber (Raw)', NULL, 15, 0.7, 3.6, 0.1, 0.5, 100.0, 'per serving', NULL, 0, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Mushrooms (Sautéed)', NULL, 28, 3.0, 4.0, 0.5, 1.5, 100.0, 'per serving', NULL, 0, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Green Beans (Steamed)', NULL, 31, 1.8, 7.0, 0.2, 2.7, 100.0, 'per serving', NULL, 0, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Kale (Raw)', NULL, 33, 2.9, 6.0, 0.6, 2.0, 100.0, 'per serving', NULL, 0, 'Vegetable', NULL);")
        db.execSQL("$insertPrefix ('Cabbage (Raw)', NULL, 25, 1.3, 6.0, 0.1, 2.5, 100.0, 'per serving', NULL, 0, 'Vegetable', NULL);")

        // Carbs
        db.execSQL("$insertPrefix ('Mashed Potatoes', NULL, 110, 2.0, 15.0, 4.5, 1.5, 150.0, 'per serving', NULL, 0, 'Carbs', NULL);")
        db.execSQL("$insertPrefix ('Macaroni Salad', NULL, 200, 4.0, 22.0, 10.0, 1.0, 150.0, 'per serving', NULL, 0, 'Carbs', NULL);")
        db.execSQL("$insertPrefix ('Potato Salad', NULL, 180, 2.5, 18.0, 11.0, 1.5, 150.0, 'per serving', NULL, 0, 'Carbs', NULL);")
        db.execSQL("$insertPrefix ('French Baguette', NULL, 270, 9.0, 52.0, 1.5, 2.5, 50.0, 'per slice', NULL, 0, 'Carbs', NULL);")
        db.execSQL("$insertPrefix ('Sourdough Bread', NULL, 230, 8.0, 45.0, 1.0, 2.0, 50.0, 'per slice', NULL, 0, 'Carbs', NULL);")
        db.execSQL("$insertPrefix ('Tortilla (Flour)', NULL, 290, 8.0, 50.0, 7.0, 2.5, 50.0, 'per piece', NULL, 0, 'Carbs', NULL);")
        db.execSQL("$insertPrefix ('Tortilla (Corn)', NULL, 218, 6.0, 45.0, 3.0, 6.0, 30.0, 'per piece', NULL, 0, 'Carbs', NULL);")
        db.execSQL("$insertPrefix ('Polenta', NULL, 70, 1.5, 15.0, 0.2, 1.0, 100.0, 'per serving', NULL, 0, 'Carbs', NULL);")
        db.execSQL("$insertPrefix ('Tapioca Pudding', NULL, 130, 2.0, 22.0, 3.5, 0.0, 150.0, 'per serving', NULL, 0, 'Carbs', NULL);")
        db.execSQL("$insertPrefix ('Udon Noodles', NULL, 130, 4.0, 27.0, 0.5, 1.0, 200.0, 'per serving', NULL, 0, 'Carbs', NULL);")
        db.execSQL("$insertPrefix ('Rice Noodles', NULL, 109, 1.0, 24.0, 0.2, 1.0, 200.0, 'per serving', NULL, 0, 'Carbs', NULL);")

        // Snacks
        db.execSQL("$insertPrefix ('Tortilla Chips', NULL, 500, 7.0, 65.0, 25.0, 5.0, 30.0, 'per serving', NULL, 0, 'Snack', NULL);")
        db.execSQL("$insertPrefix ('Pretzels', NULL, 380, 10.0, 80.0, 2.0, 3.0, 30.0, 'per serving', NULL, 0, 'Snack', NULL);")
        db.execSQL("$insertPrefix ('Pita Chips', NULL, 400, 12.0, 65.0, 10.0, 4.0, 30.0, 'per serving', NULL, 0, 'Snack', NULL);")
        db.execSQL("$insertPrefix ('Fruit Snacks', NULL, 350, 0.0, 85.0, 0.0, 0.0, 25.0, 'per pouch', NULL, 0, 'Snack', NULL);")
        db.execSQL("$insertPrefix ('Rice Crackers', NULL, 390, 8.0, 85.0, 2.0, 1.0, 30.0, 'per serving', NULL, 0, 'Snack', NULL);")
        db.execSQL("$insertPrefix ('Mixed Olives', NULL, 145, 1.0, 4.0, 15.0, 3.0, 50.0, 'per serving', NULL, 0, 'Snack', NULL);")
        db.execSQL("$insertPrefix ('Edamame', NULL, 120, 11.0, 10.0, 5.0, 5.0, 100.0, 'per serving', NULL, 0, 'Snack', NULL);")
        db.execSQL("$insertPrefix ('Cheese Sticks (String Cheese)', NULL, 280, 24.0, 4.0, 20.0, 0.0, 28.0, 'per piece', NULL, 0, 'Snack', NULL);")
        db.execSQL("$insertPrefix ('Pork Rinds', NULL, 540, 60.0, 0.0, 30.0, 0.0, 15.0, 'per serving', NULL, 0, 'Snack', NULL);")
        db.execSQL("$insertPrefix ('Graham Crackers', NULL, 430, 7.0, 75.0, 10.0, 3.0, 30.0, 'per serving', NULL, 0, 'Snack', NULL);")
        db.execSQL("$insertPrefix ('Jelly Beans', NULL, 375, 0.0, 93.0, 0.0, 0.0, 30.0, 'per serving', NULL, 0, 'Snack', NULL);")
        db.execSQL("$insertPrefix ('Marshmallows', NULL, 318, 2.0, 81.0, 0.2, 0.1, 30.0, 'per serving', NULL, 0, 'Snack', NULL);")

        db.execSQL("""
            INSERT INTO food_items (name, brand, calories_per_100g, protein_per_100g, carbs_per_100g, fat_per_100g, fiber_per_100g, default_serving_size_g, serving_description, barcode, is_local_bd, category, image_url)
            SELECT name, brand, calories_per_100g, protein_per_100g, carbs_per_100g, fat_per_100g, fiber_per_100g, default_serving_size_g, serving_description, barcode, is_local_bd, category, image_url
            FROM temp_food_items
            WHERE name NOT IN (SELECT name FROM food_items);
        """.trimIndent())
        
        db.execSQL("DROP TABLE temp_food_items;")
    }

    fun seedSkincareRoutines(db: SupportSQLiteDatabase) {
        val insertPrefix = "INSERT INTO skincare_routines (routine_type, step_order, product_name, product_category, active_ingredient, notes, instructions, dosage, warning, alternate_group) VALUES "

        // AM Routine
        db.execSQL("$insertPrefix ('AM', 1, 'SKIN1004 Madagascar Centella Ampoule Foam', 'Cleanser', NULL, NULL, 'Lather a pea-sized amount with water, massage gently for 30-45s, and rinse with cool water.', NULL, NULL, NULL);")
        db.execSQL("$insertPrefix ('AM', 2, 'Medicube TXA + Niacinamide 15 Serum', 'Treatment Serum', NULL, NULL, 'Apply to clean, dry skin. Wait 60s for full absorption before moisturizing.', '2-3 drops', NULL, NULL);")
        db.execSQL("$insertPrefix ('AM', 3, 'Purito Oat-In Calming Gel Cream', 'Moisturizer', NULL, NULL, 'Apply a light layer across face and neck to hydrate without heaviness.', NULL, NULL, NULL);")
        db.execSQL("$insertPrefix ('AM', 4, 'Nivea UV Super Water 50 Gel (SPF 50 / PA+++)', 'Sun Protection', NULL, NULL, 'Apply generously (approx. two finger-lengths) as the final morning step.', NULL, 'Reapply before sun exposure.', NULL);")

        // PM Routine
        db.execSQL("$insertPrefix ('PM', 1, 'CeraVe Renewing SA Cleanser', 'Cleanser', NULL, NULL, 'Massage onto damp skin for 60s to allow Salicylic Acid to dissolve sebum.', NULL, NULL, 'NightA');")
        db.execSQL("$insertPrefix ('PM', 2, 'SKIN1004 Madagascar Centella Ampoule Foam', 'Cleanser', NULL, NULL, 'Gentle, soothing cleanse with 33% Centella to prevent over-stripping.', NULL, NULL, 'NightB');")
        db.execSQL("$insertPrefix ('PM', 3, 'Medicube Azelaic Acid 16% BB Soothing Serum', 'Active Treatment', NULL, NULL, 'Ensure skin is 100% dry before applying to avoid tingling. Focus on active breakout zones.', 'Pea-sized amount', 'Apply strictly on dry skin to prevent irritation.', NULL);")
        db.execSQL("$insertPrefix ('PM', 4, 'Purito Oat-In Calming Gel Cream', 'Moisturizer', NULL, NULL, 'Apply a generous layer to lock in hydration and support overnight barrier repair.', NULL, NULL, NULL);")
    }
}
