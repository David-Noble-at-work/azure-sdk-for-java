var path = require('path');
var gulp = require('gulp');
var args = require('yargs').argv;
var colors = require('colors');
var spawn = require('child_process').spawn;
var fs = require('fs');

var mappings = {
    'compute': {
        'dir': 'azure-mgmt-compute',
        'source': 'specification/compute/resource-manager/readme.md',
        'package': 'com.microsoft.azure.management.compute',
        'args': '--payload-flattening-threshold=1 --tag=package-2017-03',
        'modeler': 'CompositeSwagger'
    },
    'eventhub': {
        'dir': 'azure-mgmt-eventhub',
        'source': 'specification/eventhub/resource-manager/readme.md',
        'package': 'com.microsoft.azure.management.eventhub',
        'args': '--payload-flattening-threshold=1 --tag=package-2017-04'
    },
    'servicefabric': {
        'dir': 'azure-mgmt-servicefabric',
        'source': 'specification/servicefabric/resource-manager/readme.md',
        'package': 'com.microsoft.azure.management.servicefabric',
        'args': '--payload-flattening-threshold=1 --tag=package-2016-09'
    },
    'notificationhubs': {
        'dir': 'azure-mgmt-notificationhubs',
        'source': 'specification/notificationhubs/resource-manager/readme.md',
        'package': 'com.microsoft.azure.management.notificationhubs',
        'args': '--payload-flattening-threshold=1 --tag=package-2017-04'
    },
    'analysisservices': {
        'dir': 'azure-mgmt-analysisservices',
        'source': 'specification/analysisservices/resource-manager/readme.md',
        'package': 'com.microsoft.azure.management.analysisservices',
        'args': '--payload-flattening-threshold=1 --tag=package-2016-05'
    },
    'automation': {
        'dir': 'azure-mgmt-automation',
        'source': 'specification/automation/resource-manager/readme.md',
        'package': 'com.microsoft.azure.management.automation',
        'args': '--payload-flattening-threshold=1 --tag=package-2015-10'
    },
    'billing': {
        'dir': 'azure-mgmt-billing',
        'source': 'specification/billing/resource-manager/readme.md',
        'package': 'com.microsoft.azure.management.billing',
        'args': '--payload-flattening-threshold=1 --tag=package-2017-04-preview'
    },
    'cognitiveservices': {
        'dir': 'azure-mgmt-cognitiveservices',
        'source': 'specification/cognitiveservices/resource-manager/readme.md',
        'package': 'com.microsoft.azure.management.cognitiveservices',
        'args': '--payload-flattening-threshold=1 --tag=package-2017-04'
    },
    'consumption': {
        'dir': 'azure-mgmt-consumption',
        'source': 'specification/consumption/resource-manager/readme.md',
        'package': 'com.microsoft.azure.management.consumption',
        'args': '--payload-flattening-threshold=1 --tag=package-2017-04-preview'
    },
    'customerinsights': {
        'dir': 'azure-mgmt-customerinsights',
        'source': 'specification/customer-insights/resource-manager/readme.md',
        'package': 'com.microsoft.azure.management.customerinsights',
        'args': '--payload-flattening-threshold=1 --tag=package-2017-04'
    },
    'devtestlab': {
        'dir': 'azure-mgmt-devtestlab',
        'source': 'specification/devtestlabs/resource-manager/readme.md',
        'package': 'com.microsoft.azure.management.devtestlab',
        'args': '--payload-flattening-threshold=1 --tag=package-2016-05'
    },
    'iothub': {
        'dir': 'azure-mgmt-devices',
        'source': 'specification/iothub/resource-manager/readme.md',
        'package': 'com.microsoft.azure.management.devices', // rename to iothub?
        'args': '--payload-flattening-threshold=1 --tag=package-2017-01'
    },
    'logic': {
        'dir': 'azure-mgmt-logic',
        'source': 'specification/logic/resource-manager/readme.md',
        'package': 'com.microsoft.azure.management.logic',
        'args': '--payload-flattening-threshold=1 --tag=package-2016-06'
    },
    'machinelearning': {
        'dir': 'azure-mgmt-machinelearning',
        'source': 'specification/machinelearning/resource-manager/readme.md',
        'package': 'com.microsoft.azure.management.machinelearning',
        'args': '--payload-flattening-threshold=1 --tag=package-webservices-2017-01'
    },
    'powerbi': {
        'dir': 'azure-mgmt-powerbi',
        'source': 'specification/powerbiembedded/resource-manager/readme.md',
        'package': 'com.microsoft.azure.management.powerbi',
        'args': '--payload-flattening-threshold=1 --tag=package-2016-01'
    },
    'recoveryservices': {
        'dir': 'azure-mgmt-recoveryservices',
        'source': 'arm-recoveryservices/compositeRecoveryServicesClient.json',
        'package': 'com.microsoft.azure.management.recoveryservices',
        'args': '--payload-flattening-threshold=1',
        'modeler': 'CompositeSwagger'
    },
    'recoveryservicesbackup': {
        'dir': 'azure-mgmt-recoveryservicesbackup',
        'source': 'arm-recoveryservicesbackup/2016-12-01/swagger/backupManagement.json',
        'package': 'com.microsoft.azure.management.recoveryservicesbackup',
        'args': '--payload-flattening-threshold=1'
    },
    'recoveryservicessiterecovery': {
        'dir': 'azure-mgmt-recoveryservicessiterecovery',
        'source': 'arm-recoveryservicessiterecovery/2016-08-10/swagger/service.json',
        'package': 'com.microsoft.azure.management.recoveryservicessiterecovery',
        'args': '--payload-flattening-threshold=1'
    },
    'relay': {
        'dir': 'azure-mgmt-relay',
        'source': 'arm-relay/2017-04-01/swagger/relay.json',
        'package': 'com.microsoft.azure.management.relay',
        'args': '--payload-flattening-threshold=1'
    },
    'servermanagement': {
        'dir': 'azure-mgmt-servermanagement',
        'source': 'arm-servermanagement/2016-07-01-preview/swagger/servermanagement.json',
        'package': 'com.microsoft.azure.management.servermanagement',
        'args': '--payload-flattening-threshold=1'
    },
    'storsimple8000series': {
        'dir': 'azure-mgmt-storsimple8000series',
        'source': 'arm-storsimple8000series/2017-06-01/swagger/storsimple.json',
        'package': 'com.microsoft.azure.management.storsimple8000series',
        'args': '--payload-flattening-threshold=1'
    },
    'streamanalytics': {
        'dir': 'azure-mgmt-streamanalytics',
        'source': 'arm-streamanalytics/compositeStreamAnalytics.json',
        'package': 'com.microsoft.azure.management.streamanalytics',
        'args': '--payload-flattening-threshold=1',
        'modeler': 'CompositeSwagger'
    },
    'graphrbac': {
        'dir': 'azure-mgmt-graph-rbac',
        'source': 'arm-graphrbac/1.6/swagger/graphrbac.json',
        'package': 'com.microsoft.azure.management.graphrbac',
        'args': '--payload-flattening-threshold=1'
    },
    'authorization': {
        'dir': 'azure-mgmt-authorization',
        'source': 'arm-authorization/2015-07-01/swagger/authorization.json',
        'package': 'com.microsoft.azure.management.authorization',
        'args': '--payload-flattening-threshold=1'
    },
    'arm-keyvault': {
        'dir': 'azure-mgmt-keyvault',
        'source': 'arm-keyvault/2015-06-01/swagger/keyvault.json',
        'package': 'com.microsoft.azure.management.keyvault',
        'args': '--payload-flattening-threshold=1'
    },
    'storage': {
        'dir': 'azure-mgmt-storage',
        'source': 'specification/storage/resource-manager/readme.md',
        'package': 'com.microsoft.azure.management.storage',
        'args': '--payload-flattening-threshold=2 --tag=package-2016-01'
    },
    'resources': {
        'dir': 'azure-mgmt-resources',
        'source': 'specification/resources/resource-manager/readme.md',
        'package': 'com.microsoft.azure.management.resources --tag=package-resources-2016-09'
    },
    'subscriptions': {
        'dir': 'azure-mgmt-resources',
        'source': 'specification/resources/resource-manager/readme.md',
        'package': 'com.microsoft.azure.management.resources --tag=package-subscriptions-2016-06'
    },
    'features': {
        'dir': 'azure-mgmt-resources',
        'source': 'specification/resources/resource-manager/readme.md',
        'package': 'com.microsoft.azure.management.resources --tag=package-features-2015-12'
    },
    'policy': {
        'dir': 'azure-mgmt-resources',
        'source': 'specification/resources/resource-manager/readme.md',
        'package': 'com.microsoft.azure.management.resources --tag=package-policy-2016-04'
    },
    'network': {
        'dir': 'azure-mgmt-network',
        'source': 'specification/network/resource-manager/readme.md',
        'package': 'com.microsoft.azure.management.network',
        'args': '--payload-flattening-threshold=1',
        'modeler': 'CompositeSwagger'
    },
    'appservice': {
        'dir': 'azure-mgmt-appservice',
        'source': 'arm-web/compositeWebAppClient.json',
        'package': 'com.microsoft.azure.management.appservice',
        'args': '--payload-flattening-threshold=1',
        'modeler': 'CompositeSwagger'
    },
    'redis': {
        'dir': 'azure-mgmt-redis',
        'source': 'arm-redis/2016-04-01/swagger/redis.json',
        'package': 'com.microsoft.azure.management.redis',
        'args': '--payload-flattening-threshold=1'
    },
    'search': {
        'dir': 'azure-mgmt-search',
        'source': 'arm-search/2015-08-19/swagger/search.json',
        'package': 'com.microsoft.azure.management.search',
        'args': '--payload-flattening-threshold=1'
    },
    'trafficmanager': {
        'dir': 'azure-mgmt-trafficmanager',
        'source': 'arm-trafficmanager/2017-05-01/swagger/trafficmanager.json',
        'package': 'com.microsoft.azure.management.trafficmanager',
        'args': '--payload-flattening-threshold=1'
    },
    'datalake.store.account': {
        'dir': 'azure-mgmt-datalake-store',
        'source': 'arm-datalake-store/account/2016-11-01/swagger/account.json',
        'package': 'com.microsoft.azure.management.datalake.store',
        'fluent': false
    },
    'datalake.analytics.account': {
        'dir': 'azure-mgmt-datalake-analytics',
        'source': 'arm-datalake-analytics/account/2016-11-01/swagger/account.json',
        'package': 'com.microsoft.azure.management.datalake.analytics',
        'fluent': false
    },
    'datalake.analytics.job': {
        'dir': 'azure-mgmt-datalake-analytics',
        'source': 'arm-datalake-analytics/job/2016-11-01/swagger/job.json',
        'package': 'com.microsoft.azure.management.datalake.analytics',
        'fluent': false
    },
    'datalake.analytics.catalog': {
        'dir': 'azure-mgmt-datalake-analytics',
        'source': 'arm-datalake-analytics/catalog/2016-11-01/swagger/catalog.json',
        'package': 'com.microsoft.azure.management.datalake.analytics',
        'fluent': false
    },
    'batchService': {
        'dir': 'azure-batch',
        'source': 'batch/2016-07-01.3.1/swagger/BatchService.json',
        'package': 'com.microsoft.azure.batch.protocol',
        'fluent': false,
        'args': '--payload-flattening-threshold=1'
    },
    'keyvault': {
        'dir': 'azure-keyvault',
        'source': 'keyvault/2015-06-01/swagger/keyvault.json',
        'package': 'com.microsoft.azure.keyvault',
        'fluent': false,
        'args': '--payload-flattening-threshold=1'
    },
    'batch': {
        'dir': 'azure-mgmt-batch',
        'source': 'arm-batch/2017-05-01/swagger/BatchManagement.json',
        'package': 'com.microsoft.azure.management.batch',
        'args': '--payload-flattening-threshold=1'
    },
    'sql': {
        'dir': 'azure-mgmt-sql',
        'source': 'arm-sql/compositeSql.json',
        'package': 'com.microsoft.azure.management.sql',
        'args': '--payload-flattening-threshold=1',
        'modeler': 'CompositeSwagger'
    },
    'cdn': {
        'dir': 'azure-mgmt-cdn',
        'source': 'arm-cdn/2016-10-02/swagger/cdn.json',
        'package': 'com.microsoft.azure.management.cdn',
        'args': '--payload-flattening-threshold=2'
    },
    'dns': {
        'dir': 'azure-mgmt-dns',
        'source': 'arm-dns/2016-04-01/swagger/dns.json',
        'package': 'com.microsoft.azure.management.dns',
        'args': '--payload-flattening-threshold=1'
    },
    'servicebus': {
        'dir': 'azure-mgmt-servicebus',
        'source': 'arm-servicebus/2015-08-01/swagger/servicebus.json',
        'package': 'com.microsoft.azure.management.servicebus',
        'args': '--payload-flattening-threshold=1'
    },
    'monitor': {
        'dir': 'azure-mgmt-monitor',
        'source': 'arm-monitor/compositeMonitorManagementClient.json',
        'package': 'com.microsoft.azure.management.monitor',
        'args': '--payload-flattening-threshold=1',
        'modeler': 'CompositeSwagger'
    },
    'monitor-dataplane': {
        'dir': 'azure-mgmt-monitor',
        'source': 'monitor/compositeMonitorClient.json',
        'package': 'com.microsoft.azure.management.monitor',
        'args': '-FT 1 -ServiceName Monitor',
        'modeler': 'CompositeSwagger'
    },
    'containerregistry': {
        'dir': 'azure-mgmt-containerregistry',
        'source': 'arm-containerregistry/2017-03-01/swagger/containerregistry.json',
        'package': 'com.microsoft.azure.management.containerregistry',
        'args': '--payload-flattening-threshold=1',
    },
    'scheduler': {
        'dir': 'azure-mgmt-scheduler',
        'source': 'arm-scheduler/2016-03-01/swagger/scheduler.json',
        'package': 'com.microsoft.azure.management.scheduler',
        'args': '-FT 1'
    },
    'cosmosdb': {
        'dir': 'azure-mgmt-cosmosdb',
        'source': 'arm-documentdb/2015-04-08/swagger/documentdb.json',
        'package': 'com.microsoft.azure.management.cosmosdb',
        'args': '--payload-flattening-threshold=1',
    }
};

gulp.task('default', function() {
    console.log("Usage: gulp codegen [--spec-root <swagger specs root>] [--projects <project names>] [--autorest <autorest info>] [--autorest-args <AutoRest arguments>]\n");
    console.log("--spec-root");
    console.log("\tRoot location of Swagger API specs, default value is \"https://raw.githubusercontent.com/Azure/azure-rest-api-specs/current\"");
    console.log("--projects\n\tComma separated projects to regenerate, default is all. List of available project names:");
    Object.keys(mappings).forEach(function(i) {
        console.log('\t' + i.magenta);
    });
    console.log("--autorest\n\tThe version of AutoRest. E.g. 1.0.1-20170222-2300-nightly, or the location of AutoRest repo, E.g. E:\\repo\\autorest");
    console.log("--autorest-args\n\tPasses additional argument to AutoRest generator");
});

var specRoot = args['spec-root'] || "https://raw.githubusercontent.com/Azure/azure-rest-api-specs/current";
var projects = args['projects'];
var autoRestVersion = 'latest'; // default
if (args['autorest'] !== undefined) {
    autoRestVersion = args['autorest'];
}
var autoRestArgs = args['autorest-args'] || '';
var autoRestExe;

gulp.task('codegen', function(cb) {
    if (autoRestVersion.match(/[0-9]+\.[0-9]+\.[0-9]+.*/) ||
        autoRestVersion == 'latest') {
            autoRestExe = 'autorest ---version=' + autoRestVersion;
            handleInput(projects, cb);
    } else {
        autoRestExe = "node " + path.join(autoRestVersion, "src/autorest-core/dist/app.js");
        handleInput(projects, cb);
    }
});

var handleInput = function(projects, cb) {
    if (projects === undefined) {
        Object.keys(mappings).forEach(function(proj) {
            codegen(proj, cb);
        });
    } else {
        projects.split(",").forEach(function(proj) {
            proj = proj.replace(/\ /g, '');
            if (mappings[proj] === undefined) {
                console.error('Invalid project name "' + proj + '"!');
                process.exit(1);
            }
            codegen(proj, cb);
        });
    }
}

var codegen = function(project, cb) {

    if (!args['preserve']) {
        const sourcesToDelete = path.join(
            mappings[project].dir,
            '/src/main/java/',
            mappings[project].package.replace(/\./g, '/'));

        deleteFolderRecursive(sourcesToDelete);
    }

    console.log('Generating "' + project + '" from spec file ' + specRoot + '/' + mappings[project].source);
    var generator = '--fluent';
    if (mappings[project].fluent !== null && mappings[project].fluent === false) {
        generator = '';
    }

    const generatorPath = args['autorest-java']
        ? `--use=${path.resolve(autoRestVersion, args['autorest-java'])} `
        : '';

    const regenManager = args['regenerate-manager'] ? ' --regenerate-manager=true ' : '';

    const outDir = path.resolve(mappings[project].dir);
    // path.join won't work if specRoot is a URL
    cmd = autoRestExe + ' ' + specRoot + "/" + mappings[project].source +
                        ' --java ' +
                        ' --azure-arm ' +
                        generator +
                        ` --namespace=${mappings[project].package} ` +
                        ` --output-folder=${outDir} ` +
                        ` --license-header=MICROSOFT_MIT_NO_CODEGEN ` +
                        generatorPath +
                        regenManager +
                        autoRestArgs;

    if (mappings[project].args !== undefined) {
        cmd = cmd + ' ' + mappings[project].args;
    }
    console.log('Command: ' + cmd);
    spawn(cmd, [], { shell: true, stdio: "inherit" });
};

var deleteFolderRecursive = function(path) {
    var header = "Code generated by Microsoft (R) AutoRest Code Generator";
    if(fs.existsSync(path)) {
        fs.readdirSync(path).forEach(function(file, index) {
            var curPath = path + "/" + file;
            if(fs.lstatSync(curPath).isDirectory()) { // recurse
                deleteFolderRecursive(curPath);
            } else { // delete file
                var content = fs.readFileSync(curPath).toString('utf8');
                if (content.indexOf(header) > -1) {
                    fs.unlinkSync(curPath);
                }
            }
        });
    }
};
