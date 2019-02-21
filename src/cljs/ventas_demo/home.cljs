(ns ventas-demo.home
  (:require
   [re-frame.core :as rf]
   [ventas.components.base :as base]
   [ventas.components.category-list :as category-list]
   [ventas.i18n :refer [i18n]]
   [ventas.routes :as routes]
   [ventas.plugins.slider.core :as plugins.slider]
   [ventas.themes.clothing.components.heading :as theme.heading]
   [ventas.themes.clothing.components.skeleton :as theme.skeleton]
   [ventas.themes.clothing.components.featured-entities :as featured-entities]
   [ventas.themes.clothing.pages.frontend :as original]
   [ventas.themes.clothing.pages.frontend.cart]
   [ventas.themes.clothing.pages.frontend.category]
   [ventas.themes.clothing.pages.frontend.checkout]
   [ventas.themes.clothing.pages.frontend.favorites]
   [ventas.themes.clothing.pages.frontend.login]
   [ventas.themes.clothing.pages.frontend.privacy-policy]
   [ventas.themes.clothing.pages.frontend.product]
   [ventas.themes.clothing.pages.frontend.profile]))

(def slider-kw :sample-slider)
(def category-list-key ::category-list)
(def product-list-key ::product-list)

(defn page []
  [theme.skeleton/skeleton
   [:div
    [plugins.slider/slider slider-kw]
    [base/container
     [category-list/category-list]
     [theme.heading/heading (i18n ::original/suggestions-of-the-week)]
     [featured-entities/main product-list-key]
     [theme.heading/heading (i18n ::original/recently-added)]
     [featured-entities/main category-list-key]]]])

(rf/reg-event-fx
 ::init
 (fn [_ _]
   {:dispatch-n [[::featured-entities/init category-list-key :category]
                 [::featured-entities/init product-list-key :product]
                 [::plugins.slider/sliders.get slider-kw]]}))

(routes/define-route!
 :frontend
 {:name ::original/page
  :url ""
  :component page
  :init-fx [::init]})
