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

const Shop: React.FC<ShopProps> = ({ leftItems, rightItems, boughtItems, buying, showBought, springs, dollars, handleBuy, sortAsc, setSortAsc }) => (
  <>
    <div style={{ display: 'flex', alignItems: 'center', marginBottom: 12 }}>
      <span style={{ fontWeight: 500, marginRight: 8 }}>Sort by price:</span>
      <button
        style={{ padding: '4px 12px', borderRadius: 6, border: '1px solid #ccc', background: '#fff', cursor: 'pointer', fontWeight: 500 }}
        onClick={() => setSortAsc(a => !a)}
      >
        {sortAsc ? 'Ascending' : 'Descending'}
      </button>
    </div>
    <div className={styles.shopListTwoColumns}>
      <div className={styles.shopList}>
        {leftItems.map((item, idx) => (
          <ShopItem
            key={item.id}
            item={item}
            bought={boughtItems[item.id]}
            isBuying={buying[item.id]}
            show={showBought[item.id]}
            spring={springs[idx * 2]}
            dollars={dollars}
            onBuy={handleBuy}
          />
        ))}
      </div>
      <div className={styles.shopList}>
        {rightItems.map((item, idx) => (
          <ShopItem
            key={item.id}
            item={item}
            bought={boughtItems[item.id]}
            isBuying={buying[item.id]}
            show={showBought[item.id]}
            spring={springs[idx * 2 + 1]}
            dollars={dollars}
            onBuy={handleBuy}
          />
        ))}
      </div>
    </div>
  </>
);

export default Shop; 