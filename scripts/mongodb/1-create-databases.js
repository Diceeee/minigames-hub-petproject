// Create game-clicker-service database with user
const gameClickerDb = db.getSiblingDB("game_clicker_service_db");

gameClickerDb.createUser({
    user: "game_clicker_service_user",
    pwd: "dev_password",
    roles: [
        { role: "dbOwner", db: "game_clicker_service_db" }
    ]
});