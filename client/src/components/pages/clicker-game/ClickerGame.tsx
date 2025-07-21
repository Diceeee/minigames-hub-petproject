import React, {RefObject, useEffect, useMemo, useRef, useState} from 'react';
import {useSprings} from '@react-spring/web';
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
    ItemResponse,
    StartGameResponse,
    UserStatisticsResponse,
} from "../../../types/gameClickerTypes";
import {useApi} from "../../contexts/ApiContext";

const shopItems = [
    {
        id: 1,
        name: 'Lemonade Stand',
        description: 'Start your business journey with a classic!',
        price: 50,
        perClick: 1,
        perMinute: 5,
    },
    {
        id: 2,
        name: 'Hot Dog Cart',
        description: 'Serve delicious hot dogs and earn more.',
        price: 200,
        perClick: 5,
        perMinute: 20,
    },
    {
        id: 3,
        name: 'IT-School',
        description: 'Open your own IT-School and inspire the next generation.',
        price: 10000,
        perClick: 50,
        perMinute: 200,
    },
    {
        id: 4,
        name: 'Coffee Shop',
        description: 'Brew coffee and boost productivity.',
        price: 500,
        perClick: 8,
        perMinute: 30,
    },
    {
        id: 5,
        name: 'Bookstore',
        description: 'Share knowledge and earn passive income.',
        price: 1200,
        perClick: 12,
        perMinute: 40,
    },
    {
        id: 6,
        name: 'Bakery',
        description: 'Bake bread and sweeten your profits.',
        price: 2500,
        perClick: 20,
        perMinute: 60,
    },
    {
        id: 7,
        name: 'Car Wash',
        description: 'Clean cars and clean up in business.',
        price: 4000,
        perClick: 30,
        perMinute: 80,
    },
    {
        id: 8,
        name: 'Arcade',
        description: 'Let people play and watch your dollars grow.',
        price: 7000,
        perClick: 40,
        perMinute: 120,
    },
    {
        id: 9,
        name: 'Mini Golf',
        description: 'Fun for all ages, profits for you.',
        price: 9000,
        perClick: 45,
        perMinute: 150,
    },
    {
        id: 10,
        name: 'Food Truck',
        description: 'Take your food on the road and earn more.',
        price: 3000,
        perClick: 25,
        perMinute: 70,
    },
    {
        id: 11,
        name: 'Flower Shop',
        description: 'Brighten days and your bank account.',
        price: 1500,
        perClick: 15,
        perMinute: 35,
    },
    {
        id: 12,
        name: 'Pet Store',
        description: 'Sell pets and supplies for steady income.',
        price: 6000,
        perClick: 35,
        perMinute: 100,
    },
];

const achievementsMock = [
    {
        id: 'beginning',
        name: 'Beginning of Hard Work',
        description: 'Earn 1000 dollars',
        type: 'dollars',
        target: 1000,
    },
    {
        id: 'entrepreneur',
        name: 'Starting Entrepreneur',
        description: 'Earn 50000 dollars',
        type: 'dollars',
        target: 50000,
    },
    {
        id: 'director',
        name: 'Director of world-wide famous company',
        description: 'Earn 250000 dollars',
        type: 'dollars',
        target: 250000,
    },
    {
        id: 'oligarch',
        name: 'Oligarch',
        description: 'Earn 1000000 dollars',
        type: 'dollars',
        target: 1000000,
    },
    {
        id: 'it-school',
        name: 'Owner of IT-school',
        description: 'Buy IT-School',
        type: 'buy',
        target: 3, // shopItems[2].id
    },
];

interface GameState {
    achievements?: AchievementResponse[],
    achievementStates?: Map<string, AchievementState>,
    firstStart?: boolean,
    currency?: number,
    currencyIncomePerClick?: number,
    currencyIncomePerMinute?: number,
    purchasedItemsIds?: string[],
    userStatistics?: UserStatisticsResponse,
    items?: ItemResponse[],

    playerClicksBuffer: RefObject<number>,
}

const ClickerGame: React.FC = () => {
    const [dollars, setDollars] = useState(0);
    const [dollarsPerClick, setDollarsPerClick] = useState(1);
    const [dollarsPerMinute, setDollarsPerMinute] = useState(0);
    const [activeTab, setActiveTab] = useState<'shop' | 'stats' | 'achievements'>('shop');
    const [sortAsc, setSortAsc] = useState(true);

    // Statistics state
    const [totalDollarsEarned, setTotalDollarsEarned] = useState(0);
    const [totalDollarsSpent, setTotalDollarsSpent] = useState(0);
    const [totalClicksDone, setTotalClicksDone] = useState(0);

    // Shop state
    const [boughtItems, setBoughtItems] = useState<{ [id: number]: boolean }>({});
    const [buying, setBuying] = useState<{ [id: number]: boolean }>({});
    const [showBought, setShowBought] = useState<{ [id: number]: boolean }>({});

    const {api} = useApi();
    const [gameState, setGameState] = useState<GameState>({playerClicksBuffer: useRef<number>(0)});

    useEffect(() => {
        api.get('/public/game-clicker/achievement').then(response => {
            const achievementsResponse = response.data as AchievementResponse[];
            setGameState((prevGameState) => ({...prevGameState, achievements: achievementsResponse}));
        });

        api.get('/public/game-clicker/items').then(response => {
            const itemsResponse = response.data as ItemResponse[];
            setGameState((prevGameState) => ({...prevGameState, items: itemsResponse}));
        })

        api.post('/public/game-clicker/start').then(response => {
            const startGameResponse = response.data as StartGameResponse;
            const newStates = new Map(gameState?.achievementStates);
            startGameResponse.achievementStates.forEach(state => newStates.set(state.achievementId, state));

            setGameState((prevGameState) => ({
                ...prevGameState,
                firstStart: startGameResponse.firstStart,
                currency: startGameResponse.currency,
                currencyIncomePerMinute: startGameResponse.currencyIncomePerMinute,
                currencyIncomePerClick: startGameResponse.currencyIncomePerClick,
                purchasedItemsIds: startGameResponse.purchasedItemsIds,
                userStatistics: startGameResponse.userStatistics,
                achievementStates: newStates,
            }));
        });
    }, []);

    useEffect(() => {
        const processClicksIntervalId = setInterval(() => {
            const clicksPerformed = gameState.playerClicksBuffer.current;
            if (clicksPerformed > 0) {
                gameState.playerClicksBuffer.current = 0;

                api.post('/public/game-clicker/clicks', {'clicks': clicksPerformed}).then((response) => {
                    const clicksProcessingResponse = response.data as ClicksProcessingResponse;
                    const newStates = new Map(gameState?.achievementStates);
                    clicksProcessingResponse.achievementStates.forEach(state => newStates.set(state.achievementId, state));

                    setGameState((prevState) => ({
                        ...prevState,
                        currency: clicksProcessingResponse.currencyAfterClicks,
                        achievementStates: newStates
                    }))

                    console.log(`Processed ${clicksPerformed} clicks`)
                });
            }
        }, 5000); // 5000 ms = 5 seconds

        // Cleanup on unmount
        return () => clearInterval(processClicksIntervalId);
    }, []); // Empty dependency array = run once on mount

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

    const handleClick = () => {
        setDollars(c => c + dollarsPerClick);
        setTotalDollarsEarned(e => e + dollarsPerClick);
        setTotalClicksDone(c => c + 1);
        playCoinSound();
        gameState.playerClicksBuffer.current++;
    };

    const handleBuy = (item: typeof shopItems[0]) => {
        if (dollars < item.price || boughtItems[item.id]) return;
        setBuying(b => ({...b, [item.id]: true}));
        setDollars(c => c - item.price);
        setTotalDollarsSpent(w => w + item.price);
        setTimeout(() => {
            setBoughtItems(b => ({...b, [item.id]: true}));
            setBuying(b => ({...b, [item.id]: false}));
            setShowBought(s => ({...s, [item.id]: false}));
            playBuySound(); // Play sound after successful buy
        }, 100);
        setShowBought(s => ({...s, [item.id]: true}));

        setDollarsPerClick(p => p + item.perClick);
        setDollarsPerMinute(m => m + item.perMinute);
    };

    // Achievements progress
    const getAchievementProgress = (ach: typeof achievementsMock[0]) => {
        if (ach.type === 'dollars') {
            return Math.min(totalDollarsEarned, ach.target);
        }
        if (ach.type === 'buy') {
            return boughtItems[ach.target] ? 1 : 0;
        }
        return 0;
    };

    const isAchievementDone = (ach: typeof achievementsMock[0]) => {
        if (ach.type === 'dollars') {
            return totalDollarsEarned >= ach.target;
        }
        if (ach.type === 'buy') {
            return boughtItems[ach.target];
        }
        return false;
    };

    // Create springs for all shop items, updating when showBought changes
    const springsConfig = useMemo(
        () =>
            shopItems.map(item => ({
                opacity: showBought[item.id] ? 1 : 0,
                config: {duration: 400},
            })),
        [showBought]
    );
    const springs = useSprings(shopItems.length, springsConfig);

    // Shop items sorted by price
    const sortedShopItems = useMemo(() => {
        const items = [...shopItems];
        items.sort((a, b) => sortAsc ? a.price - b.price : b.price - a.price);
        return items;
    }, [sortAsc]);

    // Split items into two columns in chess order (even index left, odd index right)
    const splitShopItems = (items: typeof shopItems) => {
        const left: typeof shopItems = [];
        const right: typeof shopItems = [];
        items.forEach((item, idx) => {
            if (idx % 2 === 0) left.push(item);
            else right.push(item);
        });
        return [left, right];
    };
    const [leftItems, rightItems] = splitShopItems(sortedShopItems);

    return (
        <div className={styles.container} style={{height: '100%'}}>
            <ClickerPanel
                dollars={dollars}
                dollarsPerClick={dollarsPerClick}
                dollarsPerMinute={dollarsPerMinute}
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
                            boughtItems={boughtItems}
                            buying={buying}
                            showBought={showBought}
                            springs={springs}
                            dollars={dollars}
                            handleBuy={handleBuy}
                            sortAsc={sortAsc}
                            setSortAsc={setSortAsc}
                        />
                    ) : activeTab === 'stats' ? (
                        <StatsPanel
                            totalDollarsEarned={totalDollarsEarned}
                            totalDollarsSpent={totalDollarsSpent}
                            totalClicksDone={totalClicksDone}
                        />
                    ) : (
                        <AchievementsPanel
                            achievements={achievementsMock}
                            isAchievementDone={isAchievementDone}
                            getAchievementProgress={getAchievementProgress}
                        />
                    )}
                </div>
            </div>
        </div>
    );
};

export default ClickerGame;