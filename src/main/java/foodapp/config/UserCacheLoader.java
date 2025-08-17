package foodapp.config;

import jakarta.annotation.PostConstruct;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import foodapp.service.UserService;

@Component
@RequiredArgsConstructor
public class UserCacheLoader {

	private final UserService userService;
	private final CacheManager cacheManager;

	@PostConstruct
	public void loadUserCache() {
		var users = userService.getAllUsers(); // fetch from DB
		var cache = cacheManager.getCache("user_cache");
		if (cache != null) {
			cache.put("ALL", users);
			System.out.println("User cache preloaded with " + users.size() + " users");
		} else {
			System.err.println("user_cache not configured!");
		}
	}
}
