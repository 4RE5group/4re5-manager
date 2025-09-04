#include "defs.h"
#include "config.h"

void	write_manifest(json_t *elem, const char *filename)
{
	json_t *manifest = json_object();

	// Copy top-level fields
	json_object_set(manifest, "disabled", json_string(json_string_value(json_object_get(elem, "disabled"))));
	json_object_set(manifest, "name", json_string(json_string_value(json_object_get(elem, "name"))));
	json_object_set(manifest, "description", json_string(json_string_value(json_object_get(elem, "description"))));
	json_object_set(manifest, "image", json_string(json_string_value(json_object_get(elem, "image"))));
	json_object_set(manifest, "first_date", json_string(json_string_value(json_object_get(elem, "first_date"))));
	json_object_set(manifest, "author", json_string(json_string_value(json_object_get(elem, "author"))));
	json_object_set(manifest, "price", json_string(json_string_value(json_object_get(elem, "price"))));
	json_object_set(manifest, "docs", json_string(json_string_value(json_object_get(elem, "docs"))));
	json_object_set(manifest, "github", json_string(json_string_value(json_object_get(elem, "github"))));
	json_object_set(manifest, "license", json_string(json_string_value(json_object_get(elem, "license"))));
	json_object_set(manifest, "package", json_string(json_string_value(json_object_get(elem, "package"))));

	// Get the Linux platform (platformid 2)
	json_t *platforms = json_object_get(elem, "platform");
	size_t index;
	json_t *value;
	json_array_foreach(platforms, index, value) {
		json_t *platformid = json_object_get(value, "platformid");
		if (json_integer_value(platformid) == 2) { // Linux
			json_object_set(manifest, "version", json_string(json_string_value(json_object_get(value, "version"))));
			json_object_set(manifest, "isCmdTool", json_string(json_string_value(json_object_get(value, "isCmdTool"))));
			json_object_set(manifest, "startUpFile", json_string(json_string_value(json_object_get(value, "startUpFile"))));
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

	putstrf("%s[%s+]%s Manifest created!%s\n", 1, COLOR_BGREEN, COLOR_GREEN, COLOR_BGREEN, COLOR_RESET);
	json_decref(manifest);
}


int	install(char	*package_name)
{
	char		package_path[512];
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
		return (0);

	const char	*package;
	short		found = 0;
	char		installation_output[512];
	json_array_foreach(root, i, value)
	{
		package = json_getkey(value, "package");
		if (strcmp(package, package_name) == 0)
		{
			json_array_foreach(json_object_get(value, "platform"), j, platform)
			{
				if (json_integer_value(json_object_get(platform, "platformid")) == PLATFORM_LINUX)
				{
					// if paid, redirect to 4re5.com
					if (strcmp(json_getkey(value, "price"), "free") != 0)
					{
						putstrf("This item is sold at %s%s%s, to aquire it, go to %s%s%s\n", 1, COLOR_BGREEN, json_getkey(value, "price"), COLOR_RESET, COLOR_BGREEN, json_getkey(platform, "url"), COLOR_RESET);
						return (0);
					}
					strformat(PACKAGES_DIR, installation_output, APP_DIR, package);
					if (access(installation_output, F_OK) == -1)
						if (!mkdir(installation_output, 0777))
							putstrf("%s[%s+%s] Successfully initialised package directory!\n", 1, COLOR_BGREEN, COLOR_GREEN, COLOR_BGREEN, COLOR_RESET);
					putstrf("Found package: %s%s%s, version: %sv%s\n%s[%s~%s] Starting installation into %s/...%s\n", 1, COLOR_BGREEN, package, COLOR_RESET, COLOR_BGREEN, json_getkey(platform, "version"), COLOR_BYELLOW, COLOR_YELLOW, COLOR_BYELLOW, installation_output, COLOR_RESET);
					
					putstrf("%s[%s~%s] Downloading %s...%s\n", 1, COLOR_BYELLOW, COLOR_YELLOW, COLOR_BYELLOW, json_getkey(platform, "url"), COLOR_RESET);

					char	output_file[512];
					char	manifest_path[512];
					char	install_command[512];
					char	*downloaded_file = basename((char *)json_getkey(platform, "url"));
					strformat("%s/%s", output_file, installation_output, downloaded_file);
					strformat("%s/manifest", manifest_path, installation_output);

					// download package
					fetch_url((char *)json_getkey(platform, "url"), output_file);
				
					// write package manifest
					write_manifest(value, manifest_path);

					// if is installer, execute installation command
					if (strcmp(json_getkey(platform, "isInstaller"), "true") == 0)
					{
						// execute install command
						strformat((char *)json_getkey(platform, "installProcess"), install_command, output_file);
						system(install_command);
					}

					// create the symlink to the startup item inside 
					///usr/sbin/itemname -> ~/.4re5 group/packages/packagename/itemname
					char		ln_command[512];
					char		startUpFile[512];
					char		links_path[512];
					char		perm_command[512];
					strformat(LINKS_DIR, links_path, (char *)json_getkey(platform, "startUpFile"));
					strformat("%s/%s", startUpFile, installation_output, (char *)json_getkey(platform, "startUpFile"));
					strformat("ln -s '%s' '%s'", ln_command, startUpFile, links_path);
					// create symlink to /usr/sbin/
					system(ln_command);
					// make downloaded file executable
					strformat("chmod +x '%s'", perm_command, startUpFile);
					system(perm_command);
					found = 1;
					break;
				}
			}
		}
	}
	return (found);	
}
