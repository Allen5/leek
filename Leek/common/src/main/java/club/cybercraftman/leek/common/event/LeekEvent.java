package club.cybercraftman.leek.common.event;

public interface LeekEvent {

    // 执行回测触发事件
    interface RUN_BACKTEST {
        String topic = "run_backtest";
        String group = "backtest";
    }

    // 执行行情数据爬取事件
    interface RUN_CREEP {
        String topic = "run_creep";
        String group = "creep";
    }

    // 交易信号生成事件
    interface TRADE_SIGNAL {
        String topic = "trade_signal";
        String group = "trade";
    }

    // 挂单事件
    interface ON_ORDER {
        String topic = "on_order";
        String group = "trade";
    }

    // 撤单事件
    interface ON_ORDER_CANCEL {
        String topic = "on_order_cancel";
        String group = "trade";
    }

    // 行情数据已获取
    interface ON_BAR_RECEIVED {
        String topic = "on_bar_received";
        String group = "etl";
    }

    // 触发ETL事件
    interface ON_ETL_TRIGGERED {
        String topic = "on_etl_triggered";
        String group = "etl";
    }

}
