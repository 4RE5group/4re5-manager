#include "defs.h"
#include "config.h"

/*
	Save the manifest of the installed package from the repository json input.
*/
void	write_manifest(json_t *elem, const char *filename)
{
	json_t *manifest = json_object();

	// Copy top-level fields
	json_t *disabled_obj = json_object_get(elem, "disabled");
	if (json_is_true(disabled_obj))
		json_object_set(manifest, "disabled", json_true());
	else
		json_object_set(manifest, "disabled", json_false());

	json_object_set_new(manifest, "name", json_string(json_string_value(json_object_get(elem, "name"))));
	json_object_set_new(manifest, "description", json_string(json_string_value(json_object_get(elem, "description"))));
	json_object_set_new(manifest, "image", json_string(json_string_value(json_object_get(elem, "image"))));
	json_object_set_new(manifest, "first_date", json_string(json_string_value(json_object_get(elem, "first_date"))));
	json_object_set_new(manifest, "author", json_string(json_string_value(json_object_get(elem, "author"))));
	json_object_set_new(manifest, "price", json_string(json_string_value(json_object_get(elem, "price"))));
	json_object_set_new(manifest, "docs", json_string(json_string_value(json_object_get(elem, "docs"))));
	json_object_set_new(manifest, "github", json_string(json_string_value(json_object_get(elem, "github"))));
	json_object_set_new(manifest, "license", json_string(json_string_value(json_object_get(elem, "license"))));
	json_object_set_new(manifest, "package", json_string(json_string_value(json_object_get(elem, "package"))));

	// Get the Linux platform
	json_t	*platforms = json_object_get(elem, "platform");
	size_t	index;
	json_t	*value;
	json_array_foreach(platforms, index, value) {
		const char *plat_name = json_string_value(json_object_get(value, "platform"));
		if (plat_name && strcmp(plat_name, "linux") == 0) {
			json_object_set_new(manifest, "version", json_string(json_string_value(json_object_get(value, "version"))));
			
			if (json_is_true(json_object_get(value, "isCmdTool")))
				json_object_set(manifest, "isCmdTool", json_true());
			else
				json_object_set(manifest, "isCmdTool", json_false());
				
			json_object_set_new(manifest, "startUpFile", json_string(json_string_value(json_object_get(value, "startUpFile"))));
			json_object_set_new(manifest, "artifactType", json_string(json_string_value(json_object_get(value, "artifactType"))));
			break;
		}
	}

	// Copy keywords
	json_object_set(manifest, "keywords", json_object_get(elem, "keywords"));

	// Write to file
	FILE *f = fopen(filename, "w");
	if (f) {
		json_dumpf(manifest, f, JSON_INDENT(2));
		fclose(f);
	}

	putstrf("%sPackage manifest created!%s\n", 1, COLOR_BYELLOW, COLOR_RESET);
	json_decref(manifest);
}

/*
	Remove installed package if it is installed.
	Returns: -1 if does not exist
	         0 if success
	         1 if could not delete
*/
int	remove_installed(char *package)
{
	char		path[PATH_SIZE];
	char		link[PATH_SIZE];

	strformat(PACKAGES_DIR, path, APP_DIR, package);
	strformat(LINKS_DIR, link, package);

	// check if package is not installed
	if (access(path, F_OK) == -1)
		return (-1);
	// check if symlink exists
	if (access(link, F_OK) != -1)
		if (!unlink(link))
			return (1);

	return (delete_directory(path));	
}

/*
	Process operations on installed packages: 
	ex: upgrade all of upgradable.
	    upgrade only few packages.
*/
int	upgrade_installed(size_t package_count, char **package_list)
{
	json_t		*root;
	json_t		*value;
	size_t		index;
	int		total = -1;
	char		path[PATH_SIZE];
	
	root = get_repo();
	if (!root)
		return (total);

	total = 0;
	// Iterate through the array
	json_array_foreach(root, index, value) 
	{
		if (json_is_object(value)) 
		{
			json_t *disabled_obj = json_object_get(value, "disabled");
			if (json_is_true(disabled_obj))
				continue;

			// extract it's package
			char	*package = (char *)json_getkey(value, "package");

			// package_count = 0 => all packages
			// package_count > 0 => specific packages
			if (package_count > 0 && !arrayContains(package_count, package_list, package))
				continue; // skip because not wanted

			// define the package path inside the 4re5 package dir
			strformat(PACKAGES_DIR, path, APP_DIR, package);
			// if exists -> it is installed
			if (access(path, F_OK) != -1)
			{
				size_t	j = 0;
				json_t	*platform;
				float	last_version = 0.0f;
				float	current_version = 0.0f;
				json_array_foreach(json_object_get(value, "platform"), j, platform)
				{
					const char *plat_name = json_string_value(json_object_get(platform, "platform"));
					if (plat_name && strcmp(plat_name, "linux") == 0)
					{
						last_version = version_to_number((char *)json_getkey(platform, "version"));
						current_version = get_package_version(package); 
						if (last_version > current_version)
						{
							putstrf("Upgrading %s...\n", 1, package);
							if (install(package))
								putstrf("%serror%s: Could not proceed with the installation of %s!\n", 2, COLOR_RED, COLOR_RESET, package);
							total++;

						}

						break;
					}
				}
			}
		}
	}
	json_decref(root);
	return (total);
}

/*
	Process the installation of a package.
*/
int	install(char	*package_name)
{
	char		package_path[PATH_SIZE];
	json_t	*root;
	json_t	*value;
	size_t	i;
	size_t	j;
	json_t	*platform;

	strformat(PACKAGES_DIR, package_path, APP_DIR, "");

	// check if ~/4re5 group/packages/ exists
	if (access(package_path, F_OK) == -1)
		if (!mkdir(package_path, 0777))
			putstrf("Successfully initialised packages directory\n", 1);
	
	// get app download url & data
	root = get_repo();
	if (!root)
		return (1);

	char		*package;
	short		found = 0;
	char		installation_output[PATH_SIZE];
	json_array_foreach(root, i, value)
	{
		package = (char *)json_getkey(value, "package");
		sanitizePath(package); // sanitize path against path injection
		if (strcmp(package, package_name) == 0)
		{
			json_array_foreach(json_object_get(value, "platform"), j, platform)
			{
				const char *plat_name = json_string_value(json_object_get(platform, "platform"));
				if (plat_name && strcmp(plat_name, "linux") == 0)
				{
					// if paid, redirect to 4re5.com
					if (strcmp(json_getkey(value, "price"), "free") != 0)
					{
						putstrf("This item is sold at %s%s%s, to aquire it, go to %s%s%s\n", 1, COLOR_BGREEN, json_getkey(value, "price"), COLOR_RESET, COLOR_BGREEN, json_getkey(platform, "url"), COLOR_RESET);
						return (1);
					}
					strformat(PACKAGES_DIR, installation_output, APP_DIR, package);
					if (access(installation_output, F_OK) == -1)
						if (!mkdir(installation_output, 0777))
							putstrf("Successfully initialised package directory!\n", 1);
					putstrf("Found package: %s%s%s, version: %sv%s%s\ninstalling into %s/...\n", 1, COLOR_BGREEN, package, COLOR_RESET, COLOR_BGREEN, json_getkey(platform, "version"), COLOR_RESET, installation_output);
					
					putstrf("Downloading %s...\n", 1, json_getkey(platform, "url"));

					char	output_file[PATH_SIZE];
					char	manifest_path[PATH_SIZE];
					char	install_command[PATH_SIZE];
					char	*downloaded_file = basename((char *)json_getkey(platform, "url"));
					strformat("%s/%s", output_file, installation_output, downloaded_file);
					strformat("%s/manifest", manifest_path, installation_output);

					// download package
					fetch_url((char *)json_getkey(platform, "url"), output_file);
				
					// Verify checksum
					const char *checksum = json_getkey(platform, "checksum");
					if (checksum)
					{
						putstrf("Verifying checksum...\n", 1);
						if (!verify_checksum(output_file, checksum))
						{
							unlink(output_file);
							puterror("Checksum verification failed!");
						}
						putstrf("Checksum verified!\n", 1);
					}

					const char *artifactType = json_getkey(platform, "artifactType");

					// if is installer (deb), execute installation command
					if (artifactType && strcmp(artifactType, "deb") == 0)
					{
						// execute install command
						strformat("dpkg -i '%s'", install_command, output_file);
						// check if an error occured
						if(system(install_command) != 0)
							puterror("An error occured while installing deb package");
					}
					else
					{
						// create the symlink to the startup item inside 
						///usr/sbin/itemname -> ~/.4re5 group/packages/packagename/itemname
						char		ln_command[PATH_SIZE];
						char		links_path[PATH_SIZE];
						char		perm_command[PATH_SIZE];
						const char *startUpFileName = json_getkey(platform, "startUpFile");
						
						if (startUpFileName && strcmp(startUpFileName, "none") != 0)
						{
							strformat(LINKS_DIR, links_path, startUpFileName);
							
							// If downloaded file is different from startUpFile, rename it
							if (strcmp(downloaded_file, startUpFileName) != 0)
							{
								char new_path[PATH_SIZE];
								strformat("%s/%s", new_path, installation_output, startUpFileName);
								rename(output_file, new_path);
								strcpy(output_file, new_path);
							}

							strformat("ln -s '%s' '%s'", ln_command, output_file, links_path);
							// create symlink to /usr/sbin/
							if (system(ln_command) != 0)
								putstrf("%serror%s: Could not create symlink of this package\n", 2, COLOR_RED, COLOR_RESET);
							// make downloaded file executable
							strformat("chmod +x '%s'", perm_command, output_file);
							system(perm_command);
						}
					}
					// write package manifest
					write_manifest(value, manifest_path);

					found = 1;
					break;
				}
			}
		}
	}
	return (found == 0);	
}
