(ns moria.core
  (:require cljsjs.mithril
            [moria.utils :refer [valid-component?]]
            [clojure.string :as string]))

(enable-console-print!)

(defn m
  ([selector]
   (js/m selector))
  ([selector attr]
   (js/m selector (clj->js attr)))
  ([selector attr & children]
   (js/m selector (clj->js attr) (clj->js children))))

(defn render
  [element vnodes]
  (.render js/m element vnodes))

(defn mount
  [element component]
  (.mount js/m element component))

(defn route
  [element root routes]
  (.route js/m element root (clj->js routes)))

(def http-methods
  #{"GET"
    "POST"
    "PUT"
    "PATCH"
    "DELETE"
    "HEAD"
    "OPTIONS"})

(defn request
  ([url]
   (request url {:method "GET"}))
  ([url {:keys [method] :as options}]
   (when (not (contains? http-methods method))
     (throw (js/Error. (str method " not in (" (string/join ", " http-methods) ")"))))
   (.request js/m url (clj->js options))))

(defn jsonp
  ([url]
   (jsonp url {}))
  ([url options]
   (.jsonp js/m url (clj->js options))) )


;;;;;;;;;;;;;;;;;;;;;;;;;

(defn component
  ([c]
   (component c {}))
  ([c attrs & args]
   {:pre [(valid-component? c)]}
   ; breaking change args are applied in Mithril
   ; m.component(component, attrs, a1, a2, a3 ... aN)
   ; in here the view / controller fn in component needs to account for
   ; this
   (.component js/m (clj->js c) (clj->js attrs) (clj->js args))))

(defn prop
  ([]
   (prop nil))
  ([val]
   (.prop js/m val)))

(defn withAttr
  [property f]
  (.withAttr js/m property f))

; breaking change in Mithril this would be m.route
; here the route fn is already overloaded enough!
(def route-config (.-route js/m))

; breaking change in Mithril this would be m.route
; the the route fn is already overloaded enough!
(defn route-to
  ([path]
   {:pre [(string? path)]}
   (.route js/m path))
  ([path params]
   {:pre [(string? path)]}
   (.route js/m path params))
  ([path params replace-history]
   {:pre [(string? path)]}
   (.route js/m path params replace-history)))

; breaking
(defn route-mode
  [mode]
  (set! (.. js/m -route -mode) mode))

; breaking
(defn route-param
  ([]
   (.param js/m.route))
  ([k]
   (.param js/m.route k)))

; breaking
(defn build-query-string
  [data]
  (.buildQueryString js/m.route (clj->js data)))

; breaking
(defn parse-query-string
  [s]
  (js->clj (.parseQueryString js/m.route s)))

; TODO: this should be channels
(defn request-sync
  [req error]
  (let [response (atom nil)]
    (-> (.request js/m (clj->js req))
        (.then #(reset! response %)
               error))
    response))

(defn deferred [] (.deferred js/m))
(defn resolve [d v] (.resolve d v))
(defn promise [d] (.-promise d))
(defn then [p f] (.then p f))
(defn sync [promises] (.sync js/m (clj->js promises)))

(defn redraw-strategy
  ([] ((.. js/m -redraw -strategy)))
  ([strategy] (.strategy js/m.redraw strategy)))

(defn start-computation [] (.startComputation js/m))
(defn end-computation   [] (.endComputation js/m))

(defn deps [mock-global] (.deps js/m mock-global))
