angular.module('emitapp.directives',[])
	
	.directive('linegraph', ['$timeout', function($timeout) { 
		return { 
			restrict: "E",
			replace: true,
			scope: { dataset: "=", valuename:"=", deviceconnected:"=" },
			template: '<div id="lineChart" style="width: 100%; height: 150px"></div>',
			link: function(scope, element, attrs) { 
				var $element = $(element);
				var generateRandomNumberBetween = function(minimum, maximum) 
				{ 
					return Math.floor(Math.random() * (maximum - minimum + 1)) + minimum;
				}; 
				var getCurrentTime = function() { 
					return ((new Date()).getTime() / 1000)|0;
				}; 

				if ($.fn.epoch) 
				{ 
					var lineChartData = [
					  {
						label: "Series 1",
						values: [{time: getCurrentTime(),y: 0}]
					  }
					];

					$timeout(function() { 
							

							var graph = $element.epoch({type:"time.line",data:lineChartData,axes: ['left', 'bottom', 'right']});
							
							
							setInterval(function() { 
								
								var value = 0; 

								if (scope.deviceconnected) { 
									
									

									if (scope.dataset && typeof scope.dataset[scope.valuename] === "number") {
										value = scope.dataset[scope.valuename]; 
									}
									else { 
										value = 0;
									}


									//console.log( scope.dataset, scope.valuename, value , typeof scope.dataset[scope.valuename] );


									graph.push([
										{time: getCurrentTime(),y: value},
									]);
								}
							},500);
					});
				}
			}
		}; 
	}])

	.directive('periodicrandomizer', ['$timeout', function($timeout) {
		return { 
			restrict: "A",
			scope: { deviceconnected: "=" },
			link: function(scope, element, attrs) { 
				
				var generateRandomNumberBetween = function(minimum, maximum) 
				{ 
					return Math.floor(Math.random() * (maximum - minimum + 1)) + minimum;
				}; 
				
				$timeout(function() { 
					var interval = parseInt(attrs.interval) || 2000;
					var minimum  = parseInt(attrs.minimum) || 1;
					var maximum  = parseInt(attrs.maximum) || 100;
					
					setInterval(function() {

						if (scope.deviceconnected) 
						{
							element.html(generateRandomNumberBetween(minimum, maximum));		
						}
						else element.html('--');

					}, interval);
				});
			}
		}; 
	}])