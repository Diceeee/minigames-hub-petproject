import React from 'react';
import { animated } from '@react-spring/web';
import styles from '../../../styles/LifeSimulator.module.css';
import type { ItemResponse } from '../../../types/gameClickerTypes';

interface ShopItemProps {
  item: ItemResponse;
  bought: boolean;
  dollars: number;
  onBuy: (item: ItemResponse) => void;
}

const ShopItem: React.FC<ShopItemProps> = ({ item, bought, dollars, onBuy }) => (
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
      <span>+{item.currencyIncomeIncreasePerClick} $ / click</span>
      <span>+{item.currencyIncomeIncreaseInMinute} $ / min</span>
    </div>
    <div className={styles.buyButtonContainer}>
      <button
        className={
          `${styles.buyButton} ${dollars < item.price && !bought ? styles.buyButtonDisabled : ''} ${bought ? styles.buyButtonBought : ''}`
        }
        disabled={bought || dollars < item.price}
        onClick={() => onBuy(item)}
      >
        {bought ? 'Bought' : 'Buy'}
      </button>
      {dollars < item.price && !bought && (
        <div className={styles.buyButtonHelper}>
          Keep clicking! You need {item.price - dollars} more dollars to buy this.
        </div>
      )}
    </div>
  </div>
);

export default ShopItem; 