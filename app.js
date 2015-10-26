/**
 * EMIT / NJIT Capstone
 *
 * @author   Dondrey Taylor <ddt7@njit.edu>
 */


/*
|--------------------------------------------------------------------------
| Dependencies
|--------------------------------------------------------------------------
|
|  General imports and dependency imports
|
*/
var port       	   	= process.env.PORT || 3000;
var env        	   	= process.env.NODE_ENV === 'production' ? 'production' : 'development';
var fs 		   	= require('fs');
var twig       		= require('twig');
var twigRender  	= twig.twig;
var express    		= require('express'); 
var emitapp    		= express();
var bodyParser 	= require('body-parser'); 
var compressor 	= require('node-minify');
var redis 		= require('redis').createClient(6379, 'emit.elasticbeanstalk.com');



/*
|--------------------------------------------------------------------------
| General Asssets
|--------------------------------------------------------------------------
|
|  Holds relative paths to assets loaded by front end
|
*/
var assets = 
{
	js:  [
		'/assets/bower_components/jquery/dist/jquery.js',
		'/assets/bower_components/angular/angular.js',
		'/assets/bower_components/angular-animate/angular-animate.js',
		'/assets/bower_components/angular-cookie/angular-cookie.js',
		'/assets/bower_components/angular-route/angular-route.js',
		'/assets/bower_components/bootstrap/dist/js/bootstrap.js',
		'http://fastly.github.io/epoch/js/d3.js',
		'http://fastly.github.io/epoch/js/epoch.js',
		'/assets/js/app/app.js',
		'/assets/js/app/directives.js',
		'/assets/js/app/controllers.js',
	],
	css: [
		'/assets/bower_components/bootstrap/dist/css/bootstrap.css',
		'/assets/bower_components/epoch/epoch.min.css',
		'/assets/stylesheets/app.css',
		'/assets/stylesheets/ammend.css',
	],
}; 


/*
|--------------------------------------------------------------------------
| Globals
|--------------------------------------------------------------------------
|
|  General global variables used by express
|
*/
var config  = 
{
	assets: assets,
	environment: env
};


/*
|--------------------------------------------------------------------------
| Render View
|--------------------------------------------------------------------------
|
|  Fetches twig view and renders it
|
*/
var renderView = function(res, view, variables) 
{
	var page;
	if (fs.existsSync(view)) 
	{ 
		fs.readFile(view, 'utf8', function(err, data) 
		{ 
			page = twigRender({data:data}).render(variables);
			res.send(page);
		});

	} else res.send('');
}



/*
|--------------------------------------------------------------------------
| Middleware
|--------------------------------------------------------------------------
|
|  Used to handle request body
|
*/
emitapp.use(bodyParser.json()); 
emitapp.use(bodyParser.urlencoded({ extended: true })); 




/*
|--------------------------------------------------------------------------
| View Setup
|--------------------------------------------------------------------------
|
|  Asset/View Configuration
|
*/
emitapp.set('view engine', 'twig');
emitapp.set('views', __dirname + '/views');
emitapp.use('/assets', express.static(__dirname + '/assets'));







/*
|--------------------------------------------------------------------------
| API
|--------------------------------------------------------------------------
| 
| Internal backend API
|
*/

emitapp.post('/api/simulator/sync', function(req, res) 
{
	var params = req.body; 
	var device   = params.deviceData;
	var prefix     = "emit:device:";

	redis.set(prefix + "provider_connected", device.providerReachable);
	redis.set(prefix + "bridge", device.deviceBridge);
	redis.set(prefix + "bridge_status", device.deviceBridgeStatus);
	redis.set(prefix + "device_name", device.deviceName);
	redis.set(prefix + "device_manufacturer", device.deviceManufacturer);
	redis.set(prefix + "device_connected", device.deviceConnected);
	redis.set(prefix + "device_battery_percent", device.deviceBatteryPercent);
	redis.set(prefix + "device_pulserate", device.devicePulseRate);
	redis.set(prefix + "device_spo2_percent", device.deviceSPO2);
	
	res.send("");
});


emitapp.get('/api/device/data', function(req, res) 
{
	var prefix = "emit:beaglebone:";
	var deviceData = {};

	redis.multi()
		.get(prefix + "provider_connected")
		.get(prefix + "bridge")
		.get(prefix + "bridge_status")
		.get(prefix + "device_name")
		.get(prefix + "device_manufacturer")
		.get(prefix + "device_connected")
		.get(prefix + "device_battery_percent")
		.get(prefix + "device_pulserate")
		.get(prefix + "device_spo2_percent")
		.exec(function(err, data ) {
			
			if (data.length >= 9) { 
				deviceData.providerReachable 	=  data[0];
				deviceData.deviceBridge 		=  data[1];
				deviceData.deviceBridgeStatus 	=  data[2];
				deviceData.deviceName 		=  data[3];
				deviceData.deviceManufacturer 	=  data[4];
				deviceData.deviceConnected 	=  data[5];
				deviceData.deviceBatteryPercent 	=  data[6];
				deviceData.devicePulseRate 	=  data[7];
				deviceData.deviceSPO2 		=  data[8];
			}

			res.json({device: deviceData});
		});

	
});


/*
|--------------------------------------------------------------------------
| Async View Routes
|--------------------------------------------------------------------------
|
| Reponsible for  rendering pages requested by Angular
|
*/
emitapp.get('/view/:view', function(req, res) 
{
	var view = 'views/pages/' + req.params.view + '.twig';
	renderView(res, view, config);
});

emitapp.get('/view/:view1/:view2', function(req, res) 
{
	var view = 'views/pages/' + req.params.view1 + '/' + req.params.view2 + '.twig';
	renderView(res, view, config);
});

emitapp.get('/view/:view1/:view2/:view3', function(req, res) 
{
	var view = 'views/pages/' + req.params.view1 + '/' + req.params.view2 + '/' + req.params.view3  + '.twig';
	renderView(res, view, config);
});





/*
|--------------------------------------------------------------------------
| Views
|--------------------------------------------------------------------------
|
|  Renders base file for Angular
|
*/
emitapp.get('/:view1', function(req, res)
{
	var view = 'views/index.twig';
	renderView(res, view, config);	
});

emitapp.get('/:view1/:view2', function(req, res)
{
	var view = 'views/index.twig';
	renderView(res, view, config);	
});

emitapp.get('/:view1/:view2/:view3', function(req, res)
{
	var view = 'views/index.twig';
	renderView(res, view, config);	
});


emitapp.get('/', function(req, res)
{
	var view = 'views/index.twig';
	renderView(res, view, config);	
});



/*
|--------------------------------------------------------------------------
| Port Listening
|--------------------------------------------------------------------------
|
|  Triggers Node.js to listen on a specified port
|
*/
emitapp.listen(port);
console.log("Server running at 0.0.0.0:" + port + "/");







