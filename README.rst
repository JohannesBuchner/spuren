==========================
Spuren
==========================

Spuren is a desktop search engine. It helps you find files by name and folder contained.

Spuren consists of a tray icon for search, and 3 shell scripts that do the mining and searching of the database.
Standard linux tools are used for this as they are extremely fast. 

Although no file content is indexed, spuren is extremely helpful because it tags each file by the folder it is contained in:
e.g. ~/Documents/private/house/bill.pdf will be found by the search terms "house pdf".


------------------
Installing
------------------

 * Copy the bin folder to a directory you want the executable to live.
 * The configuration goes into ~/.local/share/spuren/
 * You should adapt the files there: which directories to index, which directories and files to skip.

------------------
Running
------------------

 * Manually: You can run the shell scripts spuren-mine to mine, and spuren-search to search.
 * Desktop:
    * Run spuren-trayicon which launches the tray icon. The tray icon also allows you to (re-)mine the database (right-click).
    * Click the icon for a search box. 
    * Easy keyboard navigation is implemented
    * folder name abbreviation helps keeping an overview
    * Shift-clicking opens the containing folder



