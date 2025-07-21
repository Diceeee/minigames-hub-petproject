export interface ClicksProcessingResponse {
    currencyBeforeClicks: number,
    currencyAfterClicks: number,
    achievementStates: AchievementState[],
}

export interface StartGameResponse {
    currency: number,
    currencyIncomePerClick: number,
    currencyIncomePerMinute: number,
    firstStart: boolean,
    userStatistics: UserStatisticsResponse,
    achievementStates: AchievementState[],
    purchasedItemsIds: string[],
}

export interface ItemPurchaseResponse {
    currencyPayedForItemPurchase: number,
    currencyIncomePerClickBefore: number,
    currencyIncomePerClickAfter: number,
    currencyIncomePerMinuteBefore: number,
    currencyIncomePerMinuteAfter: number,
    achievementStates: AchievementState[],
}

export interface ItemResponse {
    id: string,
    name: string,
    description: string,
    price: number,
    currencyIncomeIncreaseInMinute: number,
    currencyIncomeIncreasePerClick: number,
}

export interface UserStatisticsResponse {
    totalClicks: number,
    totalCurrencyEarned: number,
    totalCurrencySpent: number,
}

export interface AchievementResponse {
    id: string,
    target: CurrencyAchievementTarget | ItemAchievementTarget,
}

export interface AchievementTarget {
    name: string,
    description: string,
}

export interface CurrencyAchievementTarget extends AchievementTarget {
    amount: number,
    currencyTargetType: CurrencyTargetType,
}

enum CurrencyTargetType {
    BALANCE = 'BALANCE',
    SPENT = 'SPENT',
    CURRENCY_IN_MINUTE_HIGHER_THAN = 'CURRENCY_IN_MINUTE_HIGHER_THAN',
    CURRENCY_PER_CLICK_HIGHER_THAN = 'CURRENCY_PER_CLICK_HIGHER_THAN',
}

export interface ItemAchievementTarget extends AchievementTarget {
    itemId: string,
    itemTargetType: ItemTargetType,
}

export enum ItemTargetType {
    BOUGHT = 'BOUGHT',
}

export interface AchievementState {
    achievementId: string,
    completed: boolean,
    progress?: AchievementProgress,
}

export interface AchievementProgress {
    current: number,
    target: number,
    delta: number,
    ratio: number,
}