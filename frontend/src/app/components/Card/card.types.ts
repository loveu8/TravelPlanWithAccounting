/**
 * CardHead 元件 Props.
 * @type {Object} ICardHeadProps
 * @property {string} title - 卡片標題
 * @property {string} imgSrc - 圖片的來源
 */
interface ICardHeadProps {
  title: string;
  imgSrc: string;
}

/**
 * CardBody 元件 Props.
 * @type {Object} ICardBodyProps
 * @property {string} title - 卡片標題
 * @property {string[]} [tags] - 標籤 (可選)
 * @property {React.ReactNode} children - 子元素
 */
interface ICardBodyProps {
  title: string;
  tags?: string[];
  children?: React.ReactNode;
}

/**
 * CardBase 元件 Props.
 * @type {Object} ICardBaseProps
 * @extends ICardHeadProps
 * @extends ICardBodyProps
 * @property {function} [handleCardClick] - 處理卡片點擊的函數
 * @property {React.ReactNode} [children] - 子元素
 */
interface ICardBaseProps extends ICardHeadProps, ICardBodyProps {
  handleCardClick: () => void;
  children?: React.ReactNode;
}

/**
 * CountryCard 元件 Props.
 * @type {Object} ICountryCardProps
 * @property {string} imgSrc - 圖片的來源
 * @property {string} href - URL連結
 * @property {string} countryName - 國家名稱
 */
interface ICountryCardProps {
  imgSrc: string;
  href: string;
  countryName: string;
}

/**
 * TravelPlanCard 元件 Props.
 * @type {Object} ITravelPlanCardProps
 * @extends ICardBaseProps
 * @property {string} location - 旅遊地點
 * @property {string} author - 作者名稱
 * @property {boolean} isBookmarked - 是否已被收藏
 * @property {function} [handleBookmarkClick] - 處理收藏按鈕點擊的函數
 */
interface ITravelPlanCardProps extends ICardBaseProps {
  location: string;
  author: string;
  isBookmarked: boolean;
  handleBookmarkClick: () => void;
}

/**
 * LandscapeCard 元件 Props.
 * @type {Object} ILandscapeCardProps
 * @extends ICardBaseProps
 * @property {string} location - 旅遊地點
 * @property {number} score - 評分
 * @property {number} evaluateCount - 評價數量
 * @property {boolean} isBookmarked - 是否已被收藏
 * @property {function} handleBookmarkClick - 處理收藏按鈕點擊的函數
 * @property {function} [handleAddScheduleClick] - 處理添加行程按鈕點擊的函數
 */
interface ILandscapeCardProps extends ICardBaseProps {
  location: string;
  score: number;
  evaluateCount: number;
  isBookmarked: boolean;
  handleBookmarkClick: () => void;
  handleAddScheduleClick: () => void;
}

/**
 * MyTravelPlanCard 元件 Props.
 * @type {Object} IMyTravelPlanCardProps
 * @extends ICardBaseProps
 * @property {string} location - 旅遊地點
 * @property {number} days - 旅遊天數
 * @property {function} handleEditClick - 處理編輯按鈕點擊的函數
 */
interface IMyTravelPlanCardProps extends ICardBaseProps {
  location: string;
  days: number;
  handleEditClick: () => void;
}

export type {
  ICardHeadProps,
  ICardBodyProps,
  ICardBaseProps,
  ICountryCardProps,
  ITravelPlanCardProps,
  ILandscapeCardProps,
  IMyTravelPlanCardProps,
};
