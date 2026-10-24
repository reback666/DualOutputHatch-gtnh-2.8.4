# 输出总成 (ME) — 项目上下文

> GTNH 1.7.10 自定义 MTE：组合「输出总线(ME)」+「输出仓(ME)」为一个部件。

## 基本信息

| 项 | 值 |
|---|---|
| 项目名 | dualoutputhatchme |
| 版本 | 见 [gradle.properties](gradle.properties) 的 `modVersion` |
| Minecraft | 1.7.10 / Forge |
| 整合包 | GTNH（GregTech New Horizons），GT5 Unofficial |
| 工作目录 | `C:\Users\29797\.zcode\workspace\default\gtnh-dual-output` |
| GitHub | https://github.com/reback666/DualOutputHatch-gtnh-2.8.4 |
| MTE ID | 25300（GTNH 第三方段 17000-29999，冲突时改这个常量） |
| 本地化键 | `hatch.dual_output.me` |
| Release | 推送标签（如 `v1.6.7`）自动触发 |

## 关键类

- `registration/Registration.java` — MTE 注册（`MTE_ID=25300`）、组装机配方；材料与 NEI 样板操作见 [README 配方](README.md#配方)。配方在 `loadComplete` 读取已注册的工作台产物，显式构建 `GTRecipe_WithAlt` 以保留容量升级版候选。
- `ClientProxy.java` — 客户端初始化；改语言文件（`LangFixer`，须在 MTE 注册前）+ OmniOcular 配置补丁
- `common/tileentities/machines/MTEDualOutputHatchME.java` — 主 MTE 类（在 GT5u 源码树下，影子仓方案）
- GUI：ModularUI（GTNH 版）

## 技术方案要点

- **影子仓 + Mixin**：GT5u 源码不在本项目内，通过影子源码仓 + Mixin 修改 GT 行为；late mixin 有三重根因坑
- **版本号覆盖**：通过 `VERSION` 环境变量，构建操作见 [README](README.md#构建)。
- **缓存上传与界面刷新约束**：见 [README 使用说明](README.md#使用说明)；物品缓存统一保存在 `cachedItems`，不再重复写入第二份。
- **依赖**（`dependencies.gradle`，均为 GTNH maven）：
  - GT5-Unofficial 5.09.51.482
  - GTNHLib 0.7.10 / StructureLib 1.4.23
  - industrialcraft-2 2.2.828-experimental
  - CodeChickenCore 1.4.7 / NotEnoughItems 2.8.44-GTNH
  - ModularUI 1.2.20 / ModularUI2 2.2.20-1.7.10
  - waila 1.8.14
  - Applied-Energistics-2-Unofficial rv3-beta-702-GTNH
  - AE2FluidCraft-Rework 1.4.114-gtnh / Postea 1.1.3

## 已解决的历史问题

| 问题 | 根因 / 修复 |
|---|---|
| 设置页崩溃（1.3.0） | 客户端调用 Synced window API → 改为同窗口页面切换 |
| 影子总线排序 bug（1.3.2） | 影子总线插到 `mOutputBusses` 队首致顺序反 → 改为追加 |
| 诊断日志崩溃（1.4.1） | `Pos2d.getX()` 返回 float 传给 `%d` → 强转 int + try-catch |
| NEI 交互失败（1.4.x） | 用 `GTUtility.getFluidDisplayStack` 作流体显示栈 + `IHasStackUnderMouse` |
| 流体过滤为空（1.6.0） | GT 气体单元不在 Forge 流体容器注册表 → 加 `IFluidContainerItem` 后备 |
| 流体缓存不排空 | 未完成条目保留并在后续周期重试；`fluidAEInsert` 的 1000 是耗电换算，并非单次传输量上限，不需要逐种循环 512 次 |
| 标记不持久化（1.6.1/2） | 自绘控件点击仅客户端 → 加不可见 ButtonWidget（两端执行） |
| 具体过滤格常显 / 流体放不进 / tooltip 缺失 | 逐一修复；tooltip 用 NEI 原生样式（灰色信息） |
| 过滤格数量 | 具体过滤的物品和流体格均扩展到 18 |
| CI 失败 "Some checks were not successful" | `Test no errors reported during server run` 检查服务器日志致命错误；GT5u dev jar 含 tectech，tectech 需 YAMCore（不在 GTNH maven，404）→ 删除模板专用的 `release-packages.yml` + 共享工作流跳过服务器运行测试（`client-only`） |
| Git 推送失败 | 本地是模板的浅克隆，对象链不完整 → 删旧 `.git`，重建完整仓库历史 |
| Release 创建报错 "tag name can't be blank" | 手动建 Release 未填标签名 → 改为推送标签自动创建 |

## CI 工作流

- `.github/workflows/build-and-test.yml` — 调用 `GTNewHorizons/GTNH-Actions-Workflows/.github/workflows/build-and-test.yml@master`，`secrets: inherit`，跳过服务器运行测试
- `.github/workflows/release-tags.yml` — 推送 `*` 标签触发自动发布
- ~~`release-packages.yml`~~ — 已删除（GTNH 模板专用，与本仓库无关）


## 环境坑

- **Prism 实机路径**：注意 jar 文件锁（构建输出被占用时写入失败）
- 语言文件在 `.minecraft` 根目录；`LangFixer` 必须在 MTE 注册前执行，GT 才会读到新文案
- 清理上传 GitHub 前，删除了有版权问题的反编译 GT 类文件

## 待办

无。
