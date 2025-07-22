import React from 'react';
import ShopItem from './ShopItem';
import styles from '../../../styles/LifeSimulator.module.css';
import type { ItemResponse } from '../../../types/gameClickerTypes';

interface ShopProps {
    leftItems: ItemResponse[];
    rightItems: ItemResponse[];
    boughtItems: string[];
    dollars: number;
    handleBuy: (item: ItemResponse) => void;
    sortAsc: boolean;
    setSortAsc: (v: (a: boolean) => boolean) => void;
}

const Shop: React.FC<ShopProps> = (props) => {
    return (
        <>
            <div style={{display: 'flex', alignItems: 'center', marginBottom: 12}}>
                <span style={{fontWeight: 500, marginRight: 8}}>Sort by price:</span>
                <button
                    style={{
                        padding: '4px 12px',
                        borderRadius: 6,
                        border: '1px solid #ccc',
                        background: '#fff',
                        cursor: 'pointer',
                        fontWeight: 500
                    }}
                    onClick={() => props.setSortAsc(a => !a)}
                >
                    {props.sortAsc ? 'Ascending' : 'Descending'}
                </button>
            </div>
            <div className={styles.shopListTwoColumns}>
                <div className={styles.shopList}>
                    {props.leftItems.map((item) => (
                        <ShopItem
                            key={item.id}
                            item={item}
                            bought={props.boughtItems.includes(item.id)}
                            dollars={props.dollars}
                            onBuy={props.handleBuy}
                        />
                    ))}
                </div>
                <div className={styles.shopList}>
                    {props.rightItems.map((item) => (
                        <ShopItem
                            key={item.id}
                            item={item}
                            bought={props.boughtItems.includes(item.id)}
                            dollars={props.dollars}
                            onBuy={props.handleBuy}
                        />
                    ))}
                </div>
            </div>
        </>
    );
}

export default Shop; 