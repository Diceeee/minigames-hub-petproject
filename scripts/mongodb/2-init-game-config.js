// Switch to your custom database (it will be created if not exist)
const gameClickerDb = db.getSiblingDB("game_clicker_service_db");

gameClickerDb.game_config.insertOne({
    _id: "game_config",
    basicCurrencyGainPerClick: 1,
    maxHoursAllowedForPassiveIncome: 24
});