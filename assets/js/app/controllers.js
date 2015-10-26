angular.module('emitapp.controllers',[])

	.controller('Emit', ['$scope','$rootScope', '$timeout', '$http', function($scope, $rootScope, $timeout, $http) { 
		
		$rootScope.cache = {}; 

		$rootScope.patient = {
			fullname  : "Mary Anderson",
			firstname : "Mary",
			lastname  : "Anderson",
			address   : " 47 West 13th Street New York, NY 10011", 
			homephone : "555-555-5555",
			cellphone : "123-123-1234",
			emailaddr : "mary.anderson@seniorsrule.com",
			height    : "5'4",
			weight    : "132 lbs",
			age       : "75",
			gender    : "female"
		};

		$rootScope.popup = {
			icon: "",
			text: "",
			visible: false,
			closeable: false,
			returnable: false,
			paragraph: "",
			redirectUrl: "/dashboard",
			redirectBtnText: "",
			show: function(text, icon, closeable, paragraph, redirectUrl, redirectBtnText, returnable) {
				this.text = text;
				this.icon = icon;
				this.visible = true; 
				this.closeable = closeable;
				this.paragraph = paragraph;
				this.redirectUrl = redirectUrl || "/dashboard";
				this.redirectBtnText = redirectBtnText;
			},
			hide: function() {
				this.icon = "";
				this.text = "";
				this.visible = false;
				this.closeable = false;
				this.returnable = false;
				this.paragraph = "";
				this.redirectBtnText = "";
				this.redirectUrl = "/dashboard";
			},
			redirect: function() {
				$rootScope.redirect(this.redirectUrl);
			}
		}; 

		$rootScope.users = {
			guardians: [
				{fullname:"Stacey Allison", homephone: "555-555-5555", cellphone: "123-123-1234", emailaddr:"stacy.allison@seniorcare.org", gender:"female"}, 
				{fullname:"Patrick Gordan", homephone: "555-555-5555", cellphone: "123-123-1234", emailaddr:"pgordan123@gmail.com", gender:"male"}, 
			],
			physicians: [
				{fullname:"Mike Johnson, M.D.", affiliation: "Robert Wood Johnson", primary: "555-555-5555", secondary: "123-123-1234", emailaddr:"mike.johnson@physicians.com", gender:"male"}, 
			]
		}; 

		$rootScope.addUser = function(type) {
			if ($rootScope.users[type] instanceof Array) { 
				var cache = $rootScope.cache;
				switch (type) 
				{ 
					case 'guardians':
						$rootScope.users[type].push({ 
							fullname  : cache.fullname,
							homephone : cache.homephone,
							cellphone : cache.cellphone,
							emailaddr : cache.emailaddr,
							gender    : cache.gender,
						});
						$rootScope.popup.show('Guardian Added', 'icon-confirm', true);
						break;

					case 'physicians':
						$rootScope.users[type].push({ 
							fullname     : cache.fullname,
							affiliation  : cache.affiliation,
							primary      : cache.primary,
							secondary    : cache.secondary,
							emailaddr    : cache.emailaddr,
							gender       : cache.gender,
						});
						$rootScope.popup.show('Physician Added', 'icon-confirm', true);
						break;
				};
			}

			$rootScope.cache = {};
		}; 
		
		$rootScope.removeUser = function(type, index) { 
		}; 

		$rootScope.alerts = [ 
			{text:"Scheduled Reading Required", scheduled:"December 5, 2014", frequency: "Daily"},
			{text:"Add Your Physician", scheduled:"December 10, 2014", frequency: "One-Time"},
			{text:"Change Password Required", scheduled:"December 5, 2014", frequency:"One-Time"},
			{text:"Schedule Temperature Reading Required", scheduled:"December 5, 2014", frequency: "Weekly"}
		];

		$rootScope.guardians = { 
		}; 
	}])

	.controller('Vitals', ['$scope', '$timeout', '$http', function($scope, $timeout, $http) { 
	
		$scope.deviceData  = {device: { deviceConnected: false }};
		$scope.limit = 7;
		$scope.disabled = false;
		$scope.page = 1;
		$scope.title = "";
		$scope.$watch('page', function(value) { 
			switch (value) { 
				case 1:
					$scope.title = "Pulse Oximeter";
					break;

				case 2:
					$scope.title = "Thermometer";
					break;

				case 3:
					$scope.title = "Blood Glucose Meter";
					break;

				case 4:
					$scope.title = "Blood Pressure Monitor";
					break;

				case 5:
					$scope.title = "Blood Weight Scale";
					break;

				case 6:
					$scope.title = "Wireless Stethoscopes";
					break;

				case 7:
					$scope.title = "Pedometer";
					break;

			};
		});
		
		$scope.next = function() { 
			++$scope.page;
		};
		
		$scope.prev = function() { 
			--$scope.page;
		}; 

		$scope.pullDeviceData = function(callback) 
		{
			$http.get('/api/device/data').success(function(data)
			{ 
				if (typeof callback === "function") 
				{ 
					callback();
				}

				$timeout(function() { 
					
					$scope.deviceData.device.providerReachable = data.device.providerReachable === "true";
					$scope.deviceData.device.deviceBridge = data.device.deviceBridge;
					$scope.deviceData.device.deviceBridgeStatus = data.device.deviceBridgeStatus;
					$scope.deviceData.device.deviceName = data.device.deviceName;
					$scope.deviceData.device.deviceManufacturer = data.device.deviceManufacturer;
					$scope.deviceData.device.deviceConnected = data.device.deviceConnected === "true";
					$scope.deviceData.device.deviceBatteryPercent = parseInt(data.device.deviceBatteryPercent);
					$scope.deviceData.device.devicePulseRate = parseInt(data.device.devicePulseRate);
					$scope.deviceData.device.deviceSPO2 = parseInt(data.device.deviceSPO2);
					
					// Check for Device Connection
					if (!$scope.deviceData.device.deviceConnected) { 
						$scope.popup.show('Connect Device','icon-equalizer', true, 'To begin taking  your reading, please connect your device.', false);
					} 

					// Check for Low Battery
					else if ($scope.deviceData.device.deviceBatteryPercent  <= 10) 
					{ 
						$scope.popup.show('Low Battery','icon-flash', true, 'It appears your battery is at '+$scope.deviceData.device.deviceBatteryPercent+'%. Please charge it and re-connect to continue.', false);
					} 
					else { 
						$scope.popup.hide();
					}
				});
			}); 
		};

		setInterval($scope.pullDeviceData, 3000); 

	}])

	.controller('Simulator',['$scope', '$timeout', '$http', function($scope, $timeout, $http) { 
		
		var timeout;

		// Used for simulator purposes only 
		var getNumInRange = function(minimum, maximum) 
		{ 
			return Math.floor(Math.random() * (maximum - minimum + 1)) + minimum;
		}; 

		$scope.emit = { 
			providerReachable:"YES",
			deviceBridge: "Beagle Bone Black",
			deviceBridgeStatus: "Connected",
			deviceName:"",
			deviceManufacturer:"",
			deviceConnected:  true,
			deviceBatteryPercent: 80,
			devicePulseRate: 0,
			deviceSPO2: 0,
		};

		$scope.simulator = { 
			activateSPO2Reading: false,
			activatePulseRateReading: false,
			inuse: false,
			bridgeState: false
		};

		$scope.$watch('simulator.bridgeState', function(isconnected) { 
			if (isconnected) { 
				$scope.emit.deviceBridgeStatus = "Connected";
			}
			else {
				$scope.emit.deviceBridgeStatus = "Disconnected"; 
				$scope.emit.deviceConnected = false;
			}
		}); 

		$scope.$watch('simulator.inuse', function(inuse) { 
			if (!inuse) 
			{ 
				$scope.simulator.activateSPO2Reading = false;
				$scope.simulator.activatePulseRateReading = false;
				$scope.simulator.bridgeState =  true;
			

				$scope.emit = { 
					providerReachable:"YES",
					deviceBridge: "Beagle Bone Black",
					deviceBridgeStatus: "Connected",
					deviceName:"",
					deviceManufacturer:"",
					deviceConnected:  true,
					deviceBatteryPercent: 50,
					devicePulseRate: 0,
					deviceSPO2: 0,
				};
			}
			else 
			{ 
				$scope.simulator.activateSPO2Reading = false;
				$scope.simulator.activatePulseRateReading = false;
				$scope.simulator.bridgeState =  true;

				$scope.emit = { 
					providerReachable:"YES",
					deviceBridge: "Beagle Bone Black",
					deviceBridgeStatus: "Connected",
					deviceName:"Pulse Oximeter",
					deviceManufacturer:"Nonin",
					deviceConnected:  true,
					deviceBatteryPercent: 100,
					devicePulseRate: 0,
					deviceSPO2: 0,
				};
			}
		}); 

		setInterval(function() { 
			
			$timeout(function() 
			{ 
				if ($scope.simulator.activateSPO2Reading) 
				{
					$scope.emit.deviceSPO2 = getNumInRange(90,99); 
				}

				if ($scope.simulator.activatePulseRateReading) 
				{
					$scope.emit.devicePulseRate = getNumInRange(70,110); 
				}
			});

		},4000); 

		// Sync values with EMIT
		$scope.$watch('emit', function(oldVal, newVal) { 
			
			clearTimeout(timeout); 
			
			timeout = setTimeout(function() { 
				
				if (!parseInt($scope.emit.deviceBatteryPercent) || !$scope.emit.deviceBatteryPercent) {
					$scope.emit.deviceBatteryPercent = 0; 
				}

				$http.post('/api/simulator/sync', {deviceData: $scope.emit});
			
			}, 300);
		}, true);
	}])