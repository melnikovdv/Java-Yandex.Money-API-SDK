package ru.yandex.money.api;

/**
 * <p/>
 * <p/>
 * Created: 26.10.13 11:16
 * <p/>
 *
 * @author OneHalf
 */
interface CommandUrlHolder {

    CommandUrlHolder DEFAULT = new ConstantUrlHolder(ApiCommandsFacade.URI_YM_API);

    String getUrlForCommand(String commandName);

    class ConstantUrlHolder implements CommandUrlHolder {

        private final String uriYmApi;

        public ConstantUrlHolder(String uriYmApi1) {
            uriYmApi = uriYmApi1;
        }

        @Override
        public String getUrlForCommand(String commandName) {
            return uriYmApi + '/' + commandName;
        }
    }
}
