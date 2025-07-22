import React, {RefObject, useCallback, useEffect, useMemo, useRef, useState} from 'react';
import styles from '../../../styles/LifeSimulator.module.css';
import coinSound from '../../../sounds/coin.mp3';
import buySound from '../../../sounds/buy.mp3';
import ClickerPanel from './ClickerPanel';
import Shop from './Shop';
import StatsPanel from './StatsPanel';
import AchievementsPanel from './AchievementsPanel';
import type {
    AchievementResponse,
    AchievementState,
    ClicksProcessingResponse,
    ItemPurchaseResponse,
    ItemResponse,
    StartGameResponse,
    UserStatisticsResponse,
} from "../../../types/gameClickerTypes";
import { useApi } from "../../contexts/ApiContext";

// --- Utility Functions ---

/**
 * Splits an array of items into two columns for display.
 */
function splitShopItems(items: ItemResponse[]): [ItemResponse[], ItemResponse[]] {
    const left: ItemResponse[] = [];
    const right: ItemResponse[] = [];
    items.forEach((item, idx) => {
        if (idx % 2 === 0) left.push(item);
        else right.push(item);
    });
    return [left, right];
}

/**
 * Returns the progress value for an achievement.
 */
function getAchievementProgress(
    ach: AchievementResponse,
    achievementStates?: Map<string, AchievementState>
): number {
    if (!achievementStates) return 0;
    const state = achievementStates.get(ach.id);
    if (!state) return 0;
    if (state.progress && typeof state.progress.current === 'number') return state.progress.current;
    if (state.completed) {
        if (state.progress && typeof state.progress.target === 'number') {
            return state.progress.target;
        }
        // fallback: get from achievement definition
        if ('amount' in ach.target) return ach.target.amount;
        if ('itemId' in ach.target) return 1;
        return 1;
    }
    return 0;
}

/**
 * Returns the target value for an achievement.
 */
function getAchievementTarget(ach: AchievementResponse): number {
    if ('amount' in ach.target) return ach.target.amount;
    if ('itemId' in ach.target) return 1;
    return 1;
}

/**
 * Returns whether an achievement is completed.
 */
function isAchievementDone(
    ach: AchievementResponse,
    achievementStates?: Map<string, AchievementState>
): boolean {
    if (!achievementStates) return false;
    const state = achievementStates.get(ach.id);
    return !!state?.completed;
}

/**
 * Returns the progress ratio for an achievement (0-1).
 */
function getAchievementProgressRatio(
    ach: AchievementResponse,
    achievementStates?: Map<string, AchievementState>
): number {
    if (!achievementStates) return 0;
    const state = achievementStates.get(ach.id);
    return state?.progress?.ratio ?? (state?.completed ? 1 : 0);
}

// --- Main Component ---

interface GameState {
    achievements?: AchievementResponse[];
    achievementStates?: Map<string, AchievementState>;
    firstStart?: boolean;
    currency?: number;
    currencyIncomePerClick?: number;
    currencyIncomePerMinute?: number;
    purchasedItemsIds?: string[];
    userStatistics?: UserStatisticsResponse;
    items?: ItemResponse[];
    playerClicksBuffer: RefObject<number>;
}

const ClickerGame: React.FC = () => {
    // --- UI State ---
    const [activeTab, setActiveTab] = useState<'shop' | 'stats' | 'achievements'>('shop');
    const [sortAsc, setSortAsc] = useState(true);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    // --- Game State ---
    const { api } = useApi();
    const playerClicksBuffer = useRef<number>(0);
    const [gameState, setGameState] = useState<GameState>({ playerClicksBuffer });
    const [localDollars, setLocalDollars] = useState<number>(0); // client-side buffered dollars
    const [lastServerDollars, setLastServerDollars] = useState<number>(0); // last server value

    // --- Initial Data Fetch ---
    useEffect(() => {
        setLoading(true);
        setError(null);
        Promise.all([
            api.get('/public/game-clicker/achievement'),
            api.get('/public/game-clicker/items'),
            api.post('/public/game-clicker/start'),
        ])
            .then(([achievementsRes, itemsRes, startRes]) => {
                console.log('Starting game')
                const achievementsResponse = achievementsRes.data as AchievementResponse[];
                const itemsResponse = itemsRes.data as ItemResponse[];
                const startGameResponse = startRes.data as StartGameResponse;
                const newStates = new Map<string, AchievementState>();
                startGameResponse.achievementStates.forEach(state => newStates.set(state.achievementId, state));
                console.log(startGameResponse);
                setGameState({
                    achievements: achievementsResponse,
                    achievementStates: newStates,
                    firstStart: startGameResponse.firstStart,
                    currency: startGameResponse.currency,
                    currencyIncomePerMinute: startGameResponse.currencyIncomePerMinute,
                    currencyIncomePerClick: startGameResponse.currencyIncomePerClick,
                    purchasedItemsIds: startGameResponse.purchasedItemsIds,
                    userStatistics: startGameResponse.userStatistics,
                    items: itemsResponse,
                    playerClicksBuffer, // use the top-level ref
                });
                console.log(gameState);
                console.log(startGameResponse);
                setLocalDollars(startGameResponse.currency);
                setLastServerDollars(startGameResponse.currency);
            })
            .catch((e) => {
                setError('Failed to load game data.');
            })
            .finally(() => setLoading(false));
    }, []);

    // --- Process Buffered Clicks ---
    const processBufferedClicks = useCallback(async () => {
        const clicksPerformed = playerClicksBuffer.current;
        if (clicksPerformed > 0) {
            playerClicksBuffer.current = 0;
            try {
                const response = await api.post('/public/game-clicker/clicks', { clicks: clicksPerformed });
                const clicksProcessingResponse = response.data as ClicksProcessingResponse;
                const newStates = new Map(gameState?.achievementStates);
                clicksProcessingResponse.achievementStates.forEach(state => newStates.set(state.achievementId, state));
                console.log(gameState);
                setGameState((prevState) => ({
                    ...prevState,
                    currency: clicksProcessingResponse.currencyAfterClicks,
                    achievementStates: newStates,
                    userStatistics: { ...prevState.userStatistics, ...clicksProcessingResponse.userStatistics },
                }));
                setLastServerDollars(clicksProcessingResponse.currencyAfterClicks);
            } catch {
                setError('Failed to sync clicks.');
            }
        } else if (gameState.currency !== undefined && lastServerDollars !== gameState.currency) {
            // Sync local dollars if server value changed (e.g. after purchase)
            setLastServerDollars(gameState.currency);
            setLocalDollars(gameState.currency);
        }
    }, [api, gameState]);

    // --- Buffered Click Sync Logic ---
    useEffect(() => {
        const processClicksIntervalId = setInterval(() => {
            processBufferedClicks();
        }, 1000);
        return () => clearInterval(processClicksIntervalId);
    }, [api, gameState]);

    // --- Sound Effects ---
    const playCoinSound = () => {
        const audio = new Audio(coinSound);
        audio.volume = 0.5;
        audio.play();
    };
    const playBuySound = () => {
        const audio = new Audio(buySound);
        audio.volume = 0.5;
        audio.play();
    };

    // --- Handlers ---
    const handleClick = () => {
        setLocalDollars(c => c + (gameState.currencyIncomePerClick || 0));
        if (playerClicksBuffer) playerClicksBuffer.current++;
        playCoinSound();
    };

    const handleBuy = async (item: ItemResponse) => {
        if (!gameState.items || !gameState.purchasedItemsIds || !gameState.currency) return;
        if (gameState.purchasedItemsIds.includes(item.id) || localDollars < item.price) return;
        setLocalDollars(c => c - item.price);
        await processBufferedClicks();
        api.post(`/public/game-clicker/items/purchase/${item.id}`)
            .then((response) => {
                const res = response.data as ItemPurchaseResponse;
                const newStates = new Map(gameState?.achievementStates);
                if (res.achievementStates) res.achievementStates.forEach((state: AchievementState) => newStates.set(state.achievementId, state));
                setGameState((prevState) => ({
                    ...prevState,
                    currency: res.currencyPayedForItemPurchase !== undefined ? prevState.currency! - item.price : prevState.currency,
                    currencyIncomePerClick: res.currencyIncomePerClickAfter,
                    currencyIncomePerMinute: res.currencyIncomePerMinuteAfter,
                    purchasedItemsIds: [...(prevState.purchasedItemsIds || []), item.id],
                    userStatistics: { ...prevState.userStatistics, ...res.userStatistics },
                    achievementStates: newStates,
                }));
                playBuySound();
            })
            .catch(() => setError('Failed to purchase item.'));
    };

    // --- Derived Data ---
    const sortedShopItems = useMemo(() => {
        if (!gameState.items) return [];
        const items = [...gameState.items];
        items.sort((a, b) => sortAsc ? a.price - b.price : b.price - a.price);
        return items;
    }, [sortAsc, gameState.items, gameState.currency]);
    const [leftItems, rightItems] = useMemo(() => splitShopItems(sortedShopItems), [sortedShopItems]);

    // --- Render ---
    if (loading) return <div className={styles.container}>Loading...</div>;
    if (error) return <div className={styles.container} style={{ color: 'red' }}>{error}</div>;

    return (
        <div className={styles.container} style={{ height: '100%' }}>
            <ClickerPanel
                dollars={localDollars}
                dollarsPerClick={gameState.currencyIncomePerClick || 0}
                dollarsPerMinute={gameState.currencyIncomePerMinute || 0}
                onClick={handleClick}
            />
            <div className={styles.rightPanel}>
                <div className={styles.tabs}>
                    <button
                        className={`${styles.tabButton} ${activeTab === 'shop' ? styles.activeTab : ''}`}
                        onClick={() => setActiveTab('shop')}
                    >
                        Shop
                    </button>
                    <button
                        className={`${styles.tabButton} ${activeTab === 'stats' ? styles.activeTab : ''}`}
                        onClick={() => setActiveTab('stats')}
                    >
                        Statistics
                    </button>
                    <button
                        className={`${styles.tabButton} ${activeTab === 'achievements' ? styles.activeTab : ''}`}
                        onClick={() => setActiveTab('achievements')}
                    >
                        Achievements
                    </button>
                </div>
                <div className={styles.tabContent}>
                    {activeTab === 'shop' ? (
                        <Shop
                            leftItems={leftItems}
                            rightItems={rightItems}
                            boughtItems={gameState.purchasedItemsIds || []}
                            dollars={localDollars}
                            handleBuy={handleBuy}
                            sortAsc={sortAsc}
                            setSortAsc={setSortAsc}
                        />
                    ) : activeTab === 'stats' ? (
                        <StatsPanel
                            userStatistics={gameState.userStatistics}
                        />
                    ) : (
                        <AchievementsPanel
                            achievements={gameState.achievements || []}
                            achievementStates={gameState.achievementStates}
                            getAchievementProgress={ach => getAchievementProgress(ach, gameState.achievementStates)}
                            getAchievementTarget={getAchievementTarget}
                            isAchievementDone={ach => isAchievementDone(ach, gameState.achievementStates)}
                            getAchievementProgressRatio={ach => getAchievementProgressRatio(ach, gameState.achievementStates)}
                        />
                    )}
                </div>
            </div>
        </div>
    );
};

export default ClickerGame;