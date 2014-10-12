(ns whidbey.plugin
  (:require
    [leiningen.core.project :as project]))


(defn whidbey-profile
  [options]
  `{:dependencies
    [[mvxcvi/puget "RELEASE"]
     [mvxcvi/whidbey "RELEASE"]]

    :injections
    [(do (require 'puget.printer)
         (alter-var-root
           #'puget.printer/*options*
           puget.printer/merge-options
           ~options))]

    :repl-options
    {:nrepl-middleware
     [clojure.tools.nrepl.middleware.render-values/render-values]
     :nrepl-context
     {:interactive-eval {:renderer puget.printer/pprint-str}}}})

(def default-puget-options
  {:print-color true})

(def ^:dynamic *recur?* true)

(defn middleware
  [project]
  (if *recur?*
    (binding [*recur?* false]
      (let [profile (whidbey-profile (merge default-puget-options
                                            (:puget-options project)))]
        (-> project
            (project/add-profiles {::whidbey profile})
            (project/merge-profiles [::whidbey]))))
    project))
