===============================
Spuren desktop search engine
===============================

Spuren is a desktop search engine. It finds files by name and containing folder.

-------------
Philosophy
-------------

Spuren is minimalistic.

Simple search tools just search the entire filesystem (including system files like programs, configuration files, documentation) by filename (find, locate+updatedb).
Later, desktop search engines (Beagle, Tracker, recoll, NEPOMUK) offered to search the user directory using content indexing (find files by content), or with attached semantic metadata information. Metadata like tags overcame the rigid tree structures, because many files can be filed into multiple categories. However, metadata need to be maintained and mining file content can take substantial resources (CPU, IO, disk space).

Spuren realises that you already maintain a form of file tags: The folder structure a file lies in. **Each file is tagged by the names of its parent folder, its extension, and filename**. If you already maintain meaningful directory structures and file names, spuren will work well for you.

For example, Documents/house/bills/2019.pdf will be given the tags "house .pdf, bills, 2019". A search for "bills" will return this file, together with Documents/travels/bills/Frankfurt.xls. This solves the problem of choosing the directory structure {as {house,travels}/{bills,maps} or {bills,maps}/{house,travels} - each directory layer represent a semantic division, spuren does not care about the order.

----------
Features
----------

* 2 shell scripts for the mining and searching of the database (20 lines of code total!)
* extremely fast because standard linux tools are used
* Tray icon + cross-platform search UI
* GNOME shell integration (see gnome-spuren-search-provider)

------------------
Installing
------------------

1. Copy the bin/spuren folder to a directory you want the executable to live (~/.local/bin/ or /usr/bin/).
2. Make sure that directory is added to the $PATH.
3. The configuration goes into ~/.local/share/spuren/
4. You should adapt the files there: which directories to index, which directories and files to skip.

------------------
Usage
------------------

* To index, run on the shell::

  $ spuren-mine

* To search on the shell::

  $ spuren-search house bill
  $ spuren-search organi[sz]ation cv

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
  * Tracker can use a lot of CPU, and IO at random times. Spuren indexes quickly (with "find" on lowest IO&CPU priority), when you want it.

* NEPOMUK, Beagle, recoll vs Spuren: see Tracker vs. Spuren arguments
* locate/update-db vs Spuren: 

  * Both are based on Unix tools
  * locate is shell-only: Spuren provide shell-based searches and a graphical UI and GNOME integration
  * Spuren does not need root access or special dealing with non-user-visible files.

* GNOME search vs. Spuren

  * Both provide GNOME Shell search
  * GNOME search cannot be easily configured what folders and files to index, or not to index. Spuren allows regular expressions
  * GNOME search searches on-demand. Spuren looks up in a index database.
  * Spuren allows launching results, opening containing folder, and copying to clipboard

------------------
Licence
------------------
Open source (GPLv3, see LICENCE file). Let me know if you need another licence.


