export interface Item {
    id: string,
    name: string,
    description: string,
    price: number,
    currencyIncomeIncreaseInMinute: number,
    currencyIncomeIncreasePerClick: number,
}

export interface ClicksProcessingResponse {
    currencyBeforeClicks: number,
    currencyAfterClicks: number,
}

export interface StartGameResponse {
    currency: number,
    currencyIncomePerClick: number,
    currencyIncomePerMinute: number,
    firstStart: boolean,
    userStatistics: UserStatisticsResponse,
    purchasedItemsIds: string[],
}

export interface UserStatisticsResponse {
    totalClicks: number,
    totalCurrencyEarned: number,
    totalCurrencySpent: number,
}