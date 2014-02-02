package ru.yandex.money.api;

import org.apache.http.NameValuePair;

import java.util.Collection;
import java.util.Collections;

/**
 * <p/>
 * <p/>
 * Created: 26.10.13 11:16
 * <p/>
 *
 * @author OneHalf
 */
public interface CommandUrlHolder {

    CommandUrlHolder DEFAULT = new ConstantUrlHolder(ApiCommandsFacade.URI_YM_API);

    String getUrlForCommand(String commandName);

    Collection<NameValuePair> getAdditionalParams();

    class ConstantUrlHolder implements CommandUrlHolder {

        private final String uriYmApi;

        public ConstantUrlHolder(String uriYmApi1) {
            uriYmApi = uriYmApi1;
        }

        @Override
        public String getUrlForCommand(String commandName) {
            return uriYmApi + '/' + commandName;
        }

        @Override
        public Collection<NameValuePair> getAdditionalParams() {
            return Collections.emptyList();
        }
    }
}
