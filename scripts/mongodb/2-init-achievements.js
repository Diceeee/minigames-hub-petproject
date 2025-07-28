// Switch to your custom database (it will be created if not exist)
const gameClickerDb = db.getSiblingDB("game_clicker_service_db");

// Insert a few documents into a collection
gameClickerDb.achievements.insertMany([
    {
        _id: "one-hundred-dollars",
        _class: "com.dice.minigameshub.game_clicker_service.achievement.AchievementDocument",
        target: {
            _class: "com.dice.minigameshub.game_clicker_service.achievement.target.CurrencyTarget",
            name: "First one hundred dollars!",
            description: "Get your first one hundred dollars",
            currencyTargetType: "BALANCE", // BALANCE, SPENT, CURRENCY_IN_MINUTE_HIGHER_THAN, CURRENCY_PER_CLICK_HIGHER_THAN,
            amount: 100
        }
    },
    {
        _id: "lemonade-shop-owner",
        _class: "com.dice.minigameshub.game_clicker_service.achievement.AchievementDocument",
        target: {
            _class: "com.dice.minigameshub.game_clicker_service.achievement.target.ItemTarget",
            name: "Sweet Lemonade!",
            description: "Become owner of Lemonade Shop",
            itemTargetType: "BOUGHT", // BOUGHT
            itemId: "lemonade-shop"
        }
    },
]);