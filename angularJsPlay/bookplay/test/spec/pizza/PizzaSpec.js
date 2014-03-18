/**
 * Created with JetBrains WebStorm.
 * User: liuli
 * Date: 14-3-18
 * Time: 下午9:56
 * To change this template use File | Settings | File Templates.
 */
ddescribe('Pizza ordering sample to show Q & Promise API', function () {
    var Person = function (name, $log) {
        this.eat = function (food) {
            $log.info(name + " is eating delicious " + food);
        };
        this.beHungry = function (reason) {
            $log.warn(name + " is hungry because: " + reason);
        }
    }

    var $q, $exceptionHandler, $log, $rootScope;
    var servePreparedOrder, promisedOrder, pawel, pete;
    beforeEach(inject(function (_$q_, _$exceptionHandler_, _$log_, _$rootScope_) {
        $q = _$q_;
        $exceptionHandler = _$exceptionHandler_;
        $log = _$log_;
        $rootScope = _$rootScope_;
        pawel = new Person('Pawel', $log);
        pete = new Person('Pete', $log);
    }))

    describe('pizza pit', function () {
        describe('$q used directly', function () {
            it('should illustrate basic usage of $q', function () {
                var pizzaOrderFulfillment = $q.defer();
                var pizzaDelivered = pizzaOrderFulfillment.promise;
                pizzaDelivered.then(pawel.eat, pawel.beHungry);
                pizzaOrderFulfillment.resolve('Margherita');
                $rootScope.$digest();
                expect($log.info.logs).toContain(['Pawel is eating delicious Margherita']);
            });
        });

        describe('$q used in a service', function () {
            var Restaurant = function ($q, $rootScope) {
                var currentOrder;
                this.takeOrder = function (orderedItems) {
                    currentOrder = {
                        deferred: $q.defer(),
                        items: orderedItems
                    }
                    return currentOrder.deferred.promise;
                }
                this.deliverOrder = function (orderedItems) {
                    currentOrder.deferred.resolve(currentOrder.items);
                    $rootScope.$digest();
                };
                this.problemWithOrder = function (reason) {
                    currentOrder.deferred.reject(reason);
                    $rootScope.$digest();
                }
            }

            it('should illustrate promise rejection', function () {

                pizzaPit = new Restaurant($q, $rootScope);
                var pizzaDelivered = pizzaPit.takeOrder('Capricciosa');
                pizzaDelivered.then(pawel.eat, pawel.beHungry);

                pizzaPit.problemWithOrder('no Capricciosa, only Margherita left');
                expect($log.warn.logs).toContain(['Pawel is hungry because: no Capricciosa, only Margherita left']);
            });
        })
    });
});