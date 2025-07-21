import React from 'react';
import ShopItem from './ShopItem';
import styles from '../../../styles/LifeSimulator.module.css';

interface ShopProps {
    leftItems: any[];
    rightItems: any[];
    boughtItems: { [id: number]: boolean };
    buying: { [id: number]: boolean };
    showBought: { [id: number]: boolean };
    springs: any[];
    dollars: number;
    handleBuy: (item: any) => void;
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
                    {props.leftItems.map((item, idx) => (
                        <ShopItem
                            key={item.id}
                            item={item}
                            bought={props.boughtItems[item.id]}
                            isBuying={props.buying[item.id]}
                            show={props.showBought[item.id]}
                            spring={props.springs[idx * 2]}
                            dollars={props.dollars}
                            onBuy={props.handleBuy}
                        />
                    ))}
                </div>
                <div className={styles.shopList}>
                    {props.rightItems.map((item, idx) => (
                        <ShopItem
                            key={item.id}
                            item={item}
                            bought={props.boughtItems[item.id]}
                            isBuying={props.buying[item.id]}
                            show={props.showBought[item.id]}
                            spring={props.springs[idx * 2 + 1]}
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