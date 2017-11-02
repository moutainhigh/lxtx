<?php


/**
 *  memcache.class.php memcache operation class
 *
 * @copyright			(C) 2016 Wei
 * @license				
 * @lastmodify			2016-9-18
 */

final class mcache {
	private $cache;

	protected $mc_config = '';

	function __construct() {
		if (empty($this->mc_config)) {
			$this->mc_config = System::load_sys_config('memcache');
		}


		$this->cache = new Memcache();
		foreach ($this->mc_config as $server) {
			$this->cache->connect($server['ip'], $server['port']);
		}
	}

	function get($key) {
		return $this->cache->get($key);
	}

	function set($key, $data, $expire = 0) {
		return $this->cache->set($key, $data, $expire);
	}

	//increse the key by step
	//return the new value of the counter
	function increment($key, $step=1) {
		$val = $this->cache->increment($key, $step);
		if (!$val) {
			$this->set($key, 0);
			return $this->cache->increment($key, $step);
		} else {
			return $val;
		}
	}

	/**
	* decrese the key by specified step
	* return the new value of the counter 
	*/ 
	function decrement($key, $step=1) {
		return $this->cache->decrement($key, $step);
	}

	//shutdown the connection
	function close() {
		$this->cache->close();
	}

	function replace($key, $value, $expire = 0) {
		return $this->cache->replace($key, $value, $expire);
	}

	//delete the specified key, if timeout equals 0, immediately executed
	function delete($key, $timeout = 0) {
		return $this->cache->delete($key, $timeout);
	}

	function flush() {
		return $this->cache->flush();
	}
}


?>

