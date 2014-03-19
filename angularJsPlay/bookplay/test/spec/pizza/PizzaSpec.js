/**
 * Created with JetBrains WebStorm.
 * User: liuli
 * Date: 14-3-18
 * Time: 下午9:56
 * To change this template use File | Settings | File Templates.
 */
describe('Pizza ordering sample to show Q & Promise API', function () {
    var Person = function (name, $log) {
        this.eat = function (food) {
            $log.info(name + " is eating delicious " + food);
        };
        this.beHungry = function (reason) {
            $log.warn(name + " is hungry because: " + reason);
        }
    };

    var slice = function (pizza) {
        return "sliced " + pizza;
    };

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
            };
            var pizzaPit, saladBar;
            beforeEach(function () {
                pizzaPit = new Restaurant($q, $rootScope);
                saladBar = new Restaurant($q, $rootScope);
            });

            it('should illustrate promise rejection', function () {

                var pizzaDelivered = pizzaPit.takeOrder('Capricciosa');
                pizzaDelivered.then(pawel.eat, pawel.beHungry);

                pizzaPit.problemWithOrder('no Capricciosa, only Margherita left');
                expect($log.warn.logs).toContain(['Pawel is hungry because: no Capricciosa, only Margherita left']);
            });

            it('should allow callbacks aggregation', function () {
                var pizzaDelivered = pizzaPit.takeOrder('Margherita');
                pizzaDelivered.then(pawel.eat, pawel.beHungry);
                pizzaDelivered.then(pete.eat, pete.beHungry);
                pizzaPit.deliverOrder();
                expect($log.info.logs).toContain(['Pawel is eating delicious Margherita']);
                expect($log.info.logs).toContain(['Pete is eating delicious Margherita']);
            });

            it('should illustrate successful promise chaining', function () {
                pizzaPit.takeOrder('Margherita').then(slice).then(pawel.eat);
                pizzaPit.deliverOrder();
                expect($log.info.logs).toContain(['Pawel is eating delicious sliced Margherita']);
            });

            it('should illustrate promise rejection in chain', function () {
                pizzaPit.takeOrder('Capricciosa').then(slice).then(pawel.eat, pawel.beHungry);
                pizzaPit.problemWithOrder('no Capricciosa, only Margherita left');
                expect($log.warn.logs).toContain(['Pawel is hungry because: no Capricciosa, only Margherita left']);
            });

            it('should illustrate recovery from promise rejection', function () {
                var retry = function (reason) {
                    return pizzaPit.takeOrder('Margherita').then(slice);
                };
                pizzaPit.takeOrder('Capricciosa').then(slice, retry).then(pawel.eat, pawel.beHungry);
                pizzaPit.problemWithOrder('no Capricciosa, only Margherita left');
                pizzaPit.deliverOrder(); // for retry
                expect($log.info.logs).toContain(['Pawel is eating delicious sliced Margherita']);
            });

            it('should illustrate explicit rejection in chain', function () {
                var explain = function (reason) {
                    return $q.reject('ordered pizza not available');
                };
                pizzaPit.takeOrder('Capricciosa').then(slice, explain).then(pawel.eat, pawel.beHungry);
                pizzaPit.problemWithOrder('no Capricciosa, only Margherita left');

                expect($log.warn.logs).toContain(['Pawel is hungry because: ordered pizza not available']);
            });

            it('should illustrate promise aggregation', function () {
                var orderDelivered = $q.all([pizzaPit.takeOrder('Pepperoni'),
                    saladBar.takeOrder('Fresh salad')]);
                orderDelivered.then(pawel.eat, pawel.beHungry);
                pizzaPit.deliverOrder();
                saladBar.deliverOrder();
                expect($log.info.logs).toContain(['Pawel is eating delicious Pepperoni,Fresh salad']);
            });

            it('should illustrate promise aggregation when one promise fails', function () {
                var orderDelivered = $q.all([pizzaPit.takeOrder('Pepperoni'),
                    saladBar.takeOrder('Fresh lettuce')]);
                orderDelivered.then(pawel.eat, pawel.beHungry);
                pizzaPit.deliverOrder();
                saladBar.problemWithOrder('no fresh lettuce');
                expect($log.warn.logs).toContain(['Pawel is hungry because: no fresh lettuce']);
            });

            it('should illustrate promise aggregation with $q.when', function () {
                var orderDelivered = $q.all([pizzaPit.takeOrder('Pepperoni'),
                    $q.when('home made salad')]);
                orderDelivered.then(pawel.eat, pawel.beHungry);
                pizzaPit.deliverOrder();

                expect($log.info.logs).toContain(['Pawel is eating delicious Pepperoni,home made salad']);
            });
        })
    });
});