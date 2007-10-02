;;; mmm-wingsplaf.el - wings plaf class

;;;
;; 1. Get mmm-mode <http://sourceforge.net/projects/mmm-mode/>
;; 2. install it 
;; 3. copy mmm-wingsplaf.el to your site-lisp directory
;; 4. add the following code to your default.el or .emacs
;-----------------------------------------
;(require 'mmm-auto)
;(setq mmm-global-mode 'maybe)
;
;(require 'mmm-wingsplaf)
;(add-to-list 'mmm-mode-ext-classes-alist
;             '(nil "\\.plaf\\'" wingsplaf))
;-----------------------------------------
;  5. isn't perfect yet, but somehow works.

(require 'mmm-auto)
(require 'mmm-vars)

(mmm-add-group 'wingsplaf
 `((plaf-code
    :submode java
    :front "\\(<template\[^>\]*>\\)\\|\\(<%\\)"
    :back "\\(<write\[^>\]*>\\)\\|\\(%>\\)"
    )))
(provide 'mmm-wingsplaf)
