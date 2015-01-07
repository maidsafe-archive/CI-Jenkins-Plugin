var MAIDSafe = {};
MAIDSafe.grep = function(contents) {
	var lines = contents.split('\n');
	var modules = [];
	var temp;
	for(var i = 0; i < lines.length; i++) {		
		temp = lines[i].split(/url = git@github.com:(.+)\//);
		if (temp.length > 2) {
			modules.push(temp[2]);
		}		
	}
	return modules;
};
MAIDSafe.sort = (function() {
	var compare = function(a, b) {
		if (a > b) {
			return 1;
		}
		if (a < b) {
			return -1;
		}
		return 0;
	};
	var nameSorter = function(a, b) {
		return compare(a.name.toLowerCase(), b.name.toLowerCase());
	};
	var ownerSorter = function(a, b) {		
		return compare(a.owner.login.toLowerCase(), b.owner.login.toLowerCase());
	};
	this.repository = function(data) {
		data.sort(nameSorter);
	};
	this.owner = function(data) {
		data.sort(ownerSorter);
	};
	this.branches = function(data) {
		data.sort(nameSorter);
	};
	return this;
})();
MAIDSafe.loadRepoOwners = function(repoSelect) {
	var ownerSelect = jQuery(':input:eq('
			+ (jQuery(':input').index(repoSelect) + 1) + ')');
	var setOwners = function(data) {		
		var options = '';
		MAIDSafe.sort.owner(data);
		options += '<option id="' + MAIDSafe.org + '">' + MAIDSafe.org
				+ '</option>';
		for (var i = 0; i < data.length; i++) {
			options += '<option id="' + data[i].owner.login + '">'
					+ data[i].owner.login + '</option>';
		}
		ownerSelect.append(options);
		MAIDSafe.loadBranches(ownerSelect);
	};
	ownerSelect.children().remove();
	MAIDSafe.getForks(repoSelect.val(), setOwners);
};
MAIDSafe.loadBranches = function(element) {
	var ownerSelect = jQuery(element);
	var repoSelect = jQuery(':input:eq('
			+ (jQuery(':input').index(ownerSelect) - 1) + ')');
	var branchesSelect = jQuery(':input:eq('
			+ (jQuery(':input').index(ownerSelect) + 1) + ')');
	var setBranches = function(data) {
		var options = '';
		MAIDSafe.sort.branches(data);
		for (var i = 0; i < data.length; i++) {
			options += '<option id="' + data[i].name + '">' + data[i].name
					+ '</option>';
		}
		branchesSelect.append(options);
	};
	branchesSelect.children().remove();
	MAIDSafe.getBranches(repoSelect.val(), ownerSelect.val(), setBranches);
};
MAIDSafe.AddClickObserver = function(repoSelectElement) {
	this.register = function() {
		setTimeout(function() {
			MAIDSafe.loadRepos(jQuery(':input:eq('
					+ (jQuery(':input').index(repoSelectElement) + 5) + ')'));
		}, 100);
	};
};
MAIDSafe.registerEvents = function(element) {
	var buttons;
	jQuery(element).on('change', function(event) {
		MAIDSafe.loadRepoOwners(jQuery(event.target));
	});
	jQuery(':input:eq(' + (jQuery(':input').index(element) + 1) + ')').on(
			'change', function(event) {
				MAIDSafe.loadBranches(jQuery(event.target));
			});
	buttons = jQuery('button');
	jQuery(buttons[buttons.length - 3]).on('click',
			new MAIDSafe.AddClickObserver(element).register);
};
MAIDSafe.loadRepos = function(element) {
	var options = '';
	if (!MAIDSafe.hasOwnProperty('repos')) {
		return;
	}	
	for (var i = 0; i < MAIDSafe.repos.length; i++) {
		options += '<option id="' + MAIDSafe.repos[i] + '">'
				+ MAIDSafe.repos[i] + '</option>';
	}
	if (!element) {
		element = jQuery('select[repo="true"]');
	}
	jQuery(element).append(options);
	MAIDSafe.registerEvents(element);
	MAIDSafe.loadRepoOwners(element);
};
MAIDSafe.getRepositories = function() {
	var baseBranch = jQuery('#baseBranch').val();
	jQuery.ajax({		
		url : 'https://api.github.com/repos/' + MAIDSafe.org + '/' + MAIDSafe.superProjectName + '/contents/.gitmodules?ref=' + baseBranch,
		crossDomain : true,
		headers : {
			'Authorization' : 'token ' + MAIDSafe.token
		}
	}).success(function(data) {
		MAIDSafe.repos = MAIDSafe.grep(atob(data.content));
		MAIDSafe.repos.push(MAIDSafe.superProjectName);
		MAIDSafe.repos.sort();		
		MAIDSafe.loadRepos();
	})
};
MAIDSafe.getForks = function(repoName, callback) {
	jQuery.ajax(
			{
				url : 'https://api.github.com/repos/' + MAIDSafe.org + '/'
						+ repoName + '/forks',
				crossDomain : true,
				headers : {
					'Authorization' : 'token ' + MAIDSafe.token
				}
			}).success(callback);
};
MAIDSafe.getBranches = function(repo, owner, callback) {
	jQuery.ajax(
			{
				url : 'https://api.github.com/repos/' + owner + '/' + repo
						+ '/branches',
				crossDomain : true,
				headers : {
					'Authorization' : 'token ' + MAIDSafe.token
				}
			}).success(callback);
};
jQuery('document').ready(function() {
	var element = jQuery('span#tempStore');
	MAIDSafe.token = element.text();
	element.remove();
	element = jQuery('span#tempOrg');
	MAIDSafe.org = element.text();
	element.remove();
	element = jQuery('span#tempSuperProj');
	MAIDSafe.superProjectName = element.text();
	element.remove();
	MAIDSafe.getRepositories();
});
