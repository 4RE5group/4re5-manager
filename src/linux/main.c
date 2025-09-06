#include "defs.h"
#include "config.h"

/*
	Process json data into a listing function.
*/
int	load_json_repo(short only_names, const char *search_query)
{
	json_t		*root;
	size_t		index;
	json_t		*value;
	int		total_elements;
	
	total_elements = -1;

	root = get_repo();
	if (!root)
		return (total_elements);
		
	// Iterate through the array
	json_array_foreach(root, index, value) 
	{
		if (json_is_object(value)) 
		{
			const char	*disabled = json_getkey(value, "disabled");
			if (disabled && strcmp(disabled, "true") == 0)
				continue;
			
			// apply search query 
			if (search_query == 0 
					|| contains(json_getkey(value, "package"), search_query)
					|| contains(json_getkey(value, "name"), search_query)
					|| contains(json_getkey(value, "description"), search_query))
			{

				// extract version
				int	platformid = 0;
				const char	*version = "not found";
				json_t	*platform;
				json_array_foreach(json_object_get(value, "platform"), platformid, platform) 
				{
					// if linux, display it
					if (json_integer_value(json_object_get(platform, "platformid")) == PLATFORM_LINUX)
					{
						version = json_getkey(platform, "version");
						break;
					}
				}
				if (!platform || strcmp(version, "not found") == 0)
					continue;
				char	name_line[PATH_SIZE];
				total_elements++;
				strformat("%s%s%s@%sv%s    ", name_line, COLOR_YELLOW, json_getkey(value, "package"), COLOR_BGREEN, COLOR_WHITE, version);
				// fill with spaces until 60th character
				int	name_line_len = strlen(name_line) % 60;
				while (name_line_len < 60)
					name_line[name_line_len++] = ' ';
				name_line[name_line_len] = '\0';

				// display if is installed
				float	current_version = get_package_version(json_getkey(value, "package"));
				if (current_version >= 0.0f)
				{
					// if can be updated
					float	lastest_version = version_to_number((char *)json_getkey(platform, "version"));
					if (lastest_version > current_version)
						putstrf("%s ⤷ an update is available", 1, name_line, json_getkey(platform, "version"));
					else
						putstrf("%s ⤷ installed (as %s '%s')", 1, name_line, (strcmp(json_getkey(platform, "isCmdTool"), "true") == 0)?"the cli util":"the gui app", json_getkey(platform, "startUpFile"));
				}
				else
					putstrf("%s ⤷ not installed", 1, name_line);

				write(1, "\n", 1);
				if (only_names)
					continue;
				putstrf("    %sname%s:          %s\n", 1, COLOR_BGREEN, COLOR_RESET, json_getkey(value, "name"));	
				putstrf("    %sdescription%s:   %s\n", 1, COLOR_BGREEN, COLOR_RESET, json_getkey(value, "description"));
				putstrf("    %sprice%s:         %s\n", 1, COLOR_BGREEN, COLOR_RESET, json_getkey(value, "price"));
				putstrf("    %sauthor%s:        @%s\n", 1, COLOR_BGREEN, COLOR_RESET, json_getkey(value, "author"));
				putstrf("    %slicense%s:       %s\n", 1, COLOR_BGREEN, COLOR_RESET, json_getkey(value, "license"));
				putstrf("    %sfirst release%s: %s\n", 1, COLOR_BGREEN, COLOR_RESET, json_getkey(value, "first_date"));
				putstrf("    %sdocs%s:          %s\n", 1, COLOR_BGREEN, COLOR_RESET, json_getkey(value, "docs"));
				putstrf("    %sgithub url%s:    %s\n", 1, COLOR_BGREEN, COLOR_RESET, json_getkey(value, "github"));


				putstrf("\n", 1);
			}
		}
	}
	json_decref(root);
	return (total_elements);
}

/*
	Check if repository is installed, if not install it.
	Also has the possibility to override this with the force_updating flag.
*/
void	check_repo(short force_updating)
{
	int	status;

	// check if repo json file exists
	if (access(REPO_PATH, F_OK) == -1)
		force_updating = 1;

	if (force_updating == 1)
	{
		status = mkdir(APP_DIR, 0777);
		if (status != 0 && errno != EEXIST)
			puterror("Could not fetch the repository");
		// fetch the repository json file
		if (fetch_url(REPO_URL, REPO_PATH) == 1)
			puterror("Could not download the file");
		putstrf("Updated the repository successfully!\n", 1);
	}
}

int	main(int argc, char *argv[])
{
	// check if user has administrative rights
	if (geteuid() != 0)
		puterror("Could not access 4re5 working directory. Are you root?");
	if (argc == 1)
		puterror("Invalid arguments, use --help to get list of valid arguments");
	//  repo is it does not exist
	check_repo(0);
	if (strcmp(argv[1], "--help") == 0 || strcmp(argv[1], "-h") == 0) {
		putstrf("4re5 manager, version %s:%s\nUsage:  %s [option]\n        %s [option] package_name\n\n", 1, VERSION, BUILD_TYPE, argv[0], argv[0]);
		putstrf("Official 4re5 app downloader & updater with 4re5 security features for authentification and more\n\n", 1);
		putstrf("4re5 manager options:\n\
    --help or -h\n\
    list [full]\n\
    search <query>\n\
    install <package_name>\n\
    remove <package_name>\n\
    --version or -v\n\
    update\n\
    upgrade [package list]\n\
More information available at: https://github.com/4RE5group/4re5-manager\n", 1);
	}
	else if (strcmp(argv[1], "update") == 0)
		check_repo(1); // force updating repo
	else if (strcmp(argv[1], "--version") == 0 || strcmp(argv[1], "-v") == 0)
		putstrf("4re5 manager %s\nVersion build-type: %s\nVersion build date: %s\nRun '%s --help' for more info\n", 1, \
				VERSION, BUILD_TYPE, BUILD_DATE, argv[0]);
	else if (strcmp(argv[1], "list") == 0)
	{
		int total_elem = -1;
		if (argc == 2) // 4re5-manager list command
			total_elem = load_json_repo(1, 0); 
		else if (argc == 3 && strcmp(argv[2], "full") == 0) // full details
			total_elem = load_json_repo(0, 0);
		else if (argc > 3)
			puterror("Invalid argument at position id:4");
		else if (argc == 3)
			puterror("Invalid argument at position id:3, only 'full' or empty accepted");
		if(total_elem == -1)
			puterror("An error occurred while loading apps");
	}
	else if (strcmp(argv[1], "search") == 0)
	{
		if (argc >= 3)
		{
			int total_elem = load_json_repo(0, argv[2]);
			if (total_elem != -1)
				putstrf("Successfully displayed %i elements from query\n", 1, total_elem);
			else
				puterror("No result to be shown here");
		}
		else
			puterror("Please enter a search query");
	}
	else if (strcmp(argv[1], "install") == 0)
	{
		if (argc != 3)
		{
			putstrf("%serror%s: Format: %s install [package name]\n", 2, COLOR_RED, COLOR_RESET, argv[0]);
			return (1);
		}
		// try to install package
		sanitizePath(argv[2]); // sanitize against path injection
		if (!install(argv[2]))
			putstrf("Successfully installed %s on your system!\n", 1, argv[2]);
		else
		{
			putstrf("%serror%s: Cound not find requested package, try updating using '%s update'\n", 2, COLOR_RED, COLOR_RESET, argv[0]);
			return (1);
		}
	}
	else if (strcmp(argv[1], "upgrade") == 0)
	{
		char	**target = 0;
	
		if (argc > 2) // if specific packages are put into upgrade command, do them all
			target = &(argv[2]);
		putstrf("Running upgrade job on target%s '", 1, (argc == 2)?"":"s");
		int	j = 0;
		while (j < (argc - 2))
		{
			sanitizePath(argv[j + 2]); // sanitize input against path injection
			putstrf("%s%s", 1, argv[j + 2], (j == argc - 3)?"":" ");
			j++;
		}
		putstrf("%s'...\n", 1, (argc == 2)?"all":"");
		int total_packages = upgrade_installed(argc - 2, target);
		if (total_packages == -1)
			puterror("An error occured while upgrading apps");
		else
		{
			putstrf("Upgraded %i packages!\n", 1, total_packages);
			// check if all packages were found
			if (total_packages == argc - 2)
				putstrf("All packages were upgraded!\n", 1);
			else if (argc != 2)
			{
				putstrf("%serror%s: Could not find these packages:\n", 2, COLOR_RED, COLOR_RESET);
				for(int k=0; k<argc - 2; k++)
				{
					if (get_package_version(argv[k + 2]) == -1.0f)
						putstrf("    %s\n", 2, argv[k + 2]);
				}
				
			}

		}
	}
	else if (strcmp(argv[1], "remove") == 0)
	{
		if (argc == 3)
		{
			sanitizePath(argv[2]); // sanitize input against path injection
			int status = remove_installed(argv[2]);
			if (status == 0)
				putstrf("Successfully uninstalled package %s\n", 1, argv[2]);
			else if (status == -1)
				puterror("Package is not installed");
			else
				puterror("Could not remove package!");
		}
		else {
			puterror("Invalid arguments list");
		}
	}
	else
		puterror("Invalid argument at position id:1");
	return (0);
}
