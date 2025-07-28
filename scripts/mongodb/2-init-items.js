// Switch to your custom database (it will be created if not exist)
const gameClickerDb = db.getSiblingDB("game_clicker_service_db");

// Insert a few documents into a collection
gameClickerDb.items.insertMany([
    {
        _id: "lemonade-shop",
        name: "Lemonade Shop",
        description: "Become an owner of small street lemonade shop",
        price: 50,
        currencyIncomeIncreaseInMinute: 0,
        currencyIncomeIncreasePerClick: 1
    }
]);