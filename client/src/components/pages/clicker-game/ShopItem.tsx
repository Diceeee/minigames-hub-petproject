import React from 'react';
import { animated } from '@react-spring/web';
import styles from '../../../styles/LifeSimulator.module.css';

interface ShopItemProps {
  item: {
    id: number;
    name: string;
    description: string;
    price: number;
    perClick: number;
    perMinute: number;
  };
  bought: boolean;
  isBuying: boolean;
  show: boolean;
  spring: any;
  dollars: number;
  onBuy: (item: any) => void;
}

const ShopItem: React.FC<ShopItemProps> = ({ item, bought, isBuying, show, spring, dollars, onBuy }) => (
  <div
    className={
      bought
        ? `${styles.shopItem} ${styles.shopItemBought} ${styles.compactShopItem}`
        : `${styles.shopItem} ${styles.compactShopItem}`
    }
  >
    <div className={styles.itemName}>{item.name}</div>
    <div className={styles.itemDescription}>{item.description}</div>
    <div className={styles.itemDetails}>
      <span>Price: {item.price} $</span>
      <span>+{item.perClick} $ / click</span>
      <span>+{item.perMinute} $ / min</span>
    </div>
    <div className={styles.buyButtonContainer}>
      <button
        className={
          `${styles.buyButton} ${dollars < item.price && !bought && !isBuying ? styles.buyButtonDisabled : ''} ${bought ? styles.buyButtonBought : ''}`
        }
        disabled={bought || isBuying || dollars < item.price}
        onClick={() => onBuy(item)}
      >
        {bought ? 'Bought' : isBuying ? 'Buying...' : 'Buy'}
      </button>
      {dollars < item.price && !bought && !isBuying && (
        <div className={styles.buyButtonHelper}>
          Keep clicking! You need {item.price - dollars} more dollars to buy this.
        </div>
      )}
      {show && (
        <animated.div style={spring} className={styles.boughtMessage}>
          Bought!
        </animated.div>
      )}
    </div>
  </div>
);

export default ShopItem; 