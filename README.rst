===============================
Spuren desktop search engine
===============================

Spuren is a desktop search engine. It finds files by name and containing folder.

Spuren consists of a tray icon for search, and 3 shell scripts that do the mining and searching of the database.
Standard linux tools are used for this as they are extremely fast. GNOME shell integration was recently added.

Although no file content is indexed, spuren is extremely helpful because it tags each file by the folder it is contained in:
e.g. ~/Documents/private/house/bill.pdf will be found by the search terms "house pdf".


------------------
Installing
------------------

* Copy the bin folder to a directory you want the executable to live.
* The configuration goes into ~/.local/share/spuren/
* You should adapt the files there: which directories to index, which directories and files to skip.

------------------
Usage
------------------

* On the shell: You can run the shell scripts spuren-mine to mine, and spuren-search to search.
* GNOME Desktop environments: See gnome-spuren-search-provider/ folder for GNOME integration.
* Other Desktop environments:
   * Run spuren-trayicon which launches the tray icon. The tray icon also allows you to (re-)mine the database (right-click).
   * Click the icon for a search box. 
   * Easy keyboard navigation is implemented
   * folder name abbreviation helps keeping an overview
   * Shift-clicking opens the containing folder

--------------------------------
Comparison to other engines
--------------------------------

* Tracker vs. Spuren

  * Tracker provides full-text search. Spuren only indexes filenames
  * Tracker's database needs a lot of disk space. Spuren's database only stores the filenames in a gzipped text file.
  * Tracker can use a lot of CPU, and IO at random times. Spuren indexes quickly (with "find"), when you want it.

* GNOME search vs. Spuren

  * Both provide GNOME Shell search
  * GNOME search cannot be easily configured what folders and files to index, or not to index. Spuren allows regular expressions
  * Spuren allows launching results, opening containing folder, and copying to clipboard

* NEPOMUK vs Spuren: see Tracker vs. Spuren arguments
* Beagle vs Spuren: see Tracker vs. Spuren arguments
* locate/update-db vs Spuren: 

  * Both are based on Unix tools
  * locate is shell-only: Spuren provide shell-based searches and a graphical UI and GNOME integration
  * Spuren does not need root access.

------------------
Licence
------------------
Open source (GPLv3, see LICENCE file). Let me know if you need another licence.


