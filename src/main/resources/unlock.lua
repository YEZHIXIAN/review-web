if (redis.call('get', KEYS[1]) == ARGV[1]) then
    return redis.call('del', KEY[1])
end
return 0