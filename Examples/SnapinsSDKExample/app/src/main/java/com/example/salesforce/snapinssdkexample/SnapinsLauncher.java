package com.example.salesforce.snapinssdkexample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.salesforce.android.cases.core.CaseConfiguration;
import com.salesforce.android.cases.ui.CaseUI;
import com.salesforce.android.cases.ui.CaseUIClient;
import com.salesforce.android.cases.ui.CaseUIConfiguration;
import com.salesforce.android.chat.core.ChatConfiguration;
import com.salesforce.android.chat.ui.ChatUI;
import com.salesforce.android.chat.ui.ChatUIClient;
import com.salesforce.android.chat.ui.ChatUIConfiguration;
import com.salesforce.android.knowledge.core.KnowledgeConfiguration;
import com.salesforce.android.knowledge.ui.KnowledgeUI;
import com.salesforce.android.knowledge.ui.KnowledgeUIClient;
import com.salesforce.android.knowledge.ui.KnowledgeUIConfiguration;
import com.salesforce.android.service.common.utilities.control.Async;
import com.salesforce.android.sos.api.Sos;
import com.salesforce.android.sos.api.SosOptions;

/**
 * Singleton that lets you initialize and launch all Snap-ins features.
 *
 * Be sure to update the TODO constants in this class.
 */
public class SnapinsLauncher {

  // This value is used to flag any incomplete org settings below.
  // For any feature that you intend to implement, be sure to replace
  // references to this variable with a valid org setting.
  private static final String INCOMPLETE_ORG_SETTING = "TODO_UPDATE_THIS_SETTING";

  // TODO Knowledge or Case Management: Specify the community URL
  private static final String COMMUNITY_URL = INCOMPLETE_ORG_SETTING;
  // e.g. "https://COMMUNITY_PREFIX.force.com"

  // TODO Knowledge: Specify category group and root category
  private static final String CATEGORY_GROUP = INCOMPLETE_ORG_SETTING;
  // e.g. "Travel"
  private static final String ROOT_CATEGORY = INCOMPLETE_ORG_SETTING;
  // e.g. "All"

  // TODO Case Management: Specify action name
  private static final String CREATE_CASE_ACTION_NAME = INCOMPLETE_ORG_SETTING;
  // e.g. "NewCase"

  // TODO Live Agent Chat or SOS: Specify org ID and pod name
  private static final String ORG_ID = INCOMPLETE_ORG_SETTING;
  // e.g. "00BC00000003Lqz"
  private static final String POD_NAME = INCOMPLETE_ORG_SETTING;
  // e.g. "d.la.POD_NAME.salesforce.com"

  // TODO Live Agent Chat: Specify deployment ID and button ID
  private static final String CHAT_DEPLOYMENT_ID = INCOMPLETE_ORG_SETTING;
  // e.g. "0BNW0000000003F"
  private static final String CHAT_BUTTON_ID = INCOMPLETE_ORG_SETTING;
  // e.g. "357200000009MCq"

  // TODO SOS: Specify deployment ID
  private static final String SOS_DEPLOYMENT_ID = INCOMPLETE_ORG_SETTING;
  // e.g. "0BNW0000000003F"

  // Singleton instance
  private static final SnapinsLauncher theOneInstance = new SnapinsLauncher();

  private AppCompatActivity mActivity = null;
  private KnowledgeUI mKnowledgeUI = null;
  private KnowledgeUIClient mKnowledgeUIClient = null;

  /**
   * Gets the singleton instance for this class.
   */
  public static SnapinsLauncher getInstance() {
    return theOneInstance;
  }

  /**
   * Private constructor.
   */
  private SnapinsLauncher() {
  }

  /**
   * Passes the activity object to this singleton so that it can launch Snap-ins interfaces.
   *
   * @param activity The activity.
   */
  public void setActivity(AppCompatActivity activity) {
    this.mActivity = activity;
  }

  /**
   * Configures Knowledge.
   * This method only needs to be called once during the application lifetime.
   * Call it at the application scope, not in an activity.
   */
  public void initKnowledge() {
    if (mKnowledgeUI == null) {

      // Create a knowledge core configuration instance
      KnowledgeConfiguration coreConfiguration =
              KnowledgeConfiguration.create(COMMUNITY_URL);

      // Create a knowledge UI configuration instance from core instance
      KnowledgeUIConfiguration uiConfiguration =
              KnowledgeUIConfiguration.create(coreConfiguration, CATEGORY_GROUP, ROOT_CATEGORY);

      // Create a knowledge UI instance
      mKnowledgeUI = KnowledgeUI.configure(uiConfiguration);

      // Add the support home view addition
      SnapinsViewAddition viewAddition = new SnapinsViewAddition();
      mKnowledgeUI.viewAddition(viewAddition);
    }
  }

  /**
   * Displays a NOT YET CONFIGURED toast with information about what
   * the developer needs to configure.
   *
   * @param feature The name of the feature that isn't configured.
   */
  private void notYetConfiguredMessage(String feature) {
    Toast.makeText(mActivity, feature + " not yet configured. Update constant values in `SnapinsLauncher`.",
            Toast.LENGTH_LONG).show();
  }

  /**
   * Starts the Knowledge UI.
   */
  public void startKnowledge() {
    assert (mActivity != null);
    assert (mKnowledgeUI != null);

    if (COMMUNITY_URL == INCOMPLETE_ORG_SETTING ||
            CATEGORY_GROUP == INCOMPLETE_ORG_SETTING ||
            ROOT_CATEGORY == INCOMPLETE_ORG_SETTING) {
      notYetConfiguredMessage("Knowledge");
      return;
    }

    if (mKnowledgeUIClient == null) {

      // Create a knowledge client asynchronously
      mKnowledgeUI.createClient(mActivity)
              .onResult(new Async.ResultHandler<KnowledgeUIClient>() {

                @Override
                public void handleResult(Async<?> operation,
                                         KnowledgeUIClient uiClient) {

                  // Store reference to the Knowledge UI client
                  mKnowledgeUIClient = uiClient;

                  // Handle the close action
                  uiClient.addOnCloseListener(new KnowledgeUIClient.OnCloseListener() {
                    @Override
                    public void onClose() {
                      // Clear reference to the Knowledge UI client
                      Log.i("Snapins Example", "Closing Knowledge...");
                      mKnowledgeUIClient = null;
                    }
                  });

                  // Launch the UI
                  uiClient.launchHome(mActivity);
                }
              });
    }
  }

  /**
   * Shuts down Knowledge UI.
   */
  public void stopKnowledge() {
    if (mKnowledgeUIClient != null) {
      mKnowledgeUIClient.close();
    }
  }

  /**
   * Starts the Case Management UI.
   */
  public void startCases() {
    assert (mActivity != null);

    if (COMMUNITY_URL == INCOMPLETE_ORG_SETTING ||
            CREATE_CASE_ACTION_NAME == INCOMPLETE_ORG_SETTING) {
      notYetConfiguredMessage("Cases");
      return;
    }

    // Create a cases core configuration instance
    CaseConfiguration coreConfiguration =
            new CaseConfiguration.Builder(COMMUNITY_URL, CREATE_CASE_ACTION_NAME)
                    .build();

    // Create a UI configuration instance from a cases core instance
    CaseUI.with(mActivity).configure(CaseUIConfiguration.create(coreConfiguration));

    // Create a cases client UI asynchronously
    CaseUI.with(mActivity).uiClient()
            .onResult(new Async.ResultHandler<CaseUIClient>() {
              @Override
              public void handleResult(Async<?> async,
                                       @NonNull CaseUIClient caseUIClient) {
                caseUIClient.launch(mActivity);
              }
            });
  }

  /**
   * Starts the Live Agent Chat UI.
   */
  public void startChat() {
    assert (mActivity != null);

    if (ORG_ID == INCOMPLETE_ORG_SETTING ||
            POD_NAME == INCOMPLETE_ORG_SETTING ||
            CHAT_DEPLOYMENT_ID == INCOMPLETE_ORG_SETTING ||
            CHAT_BUTTON_ID == INCOMPLETE_ORG_SETTING) {
      notYetConfiguredMessage("Chat");
      return;
    }

    // First lets get rid of the Knowledge UI
    stopKnowledge();

    // Create a chat core configuration instance
    ChatConfiguration chatConfiguration =
            new ChatConfiguration.Builder(ORG_ID, CHAT_BUTTON_ID,
                    CHAT_DEPLOYMENT_ID, POD_NAME)
                    .build();

    // Create a UI configuration instance from a chat core config object
    // and start session!
    ChatUI.configure(ChatUIConfiguration.create(chatConfiguration))
            .createClient(mActivity.getApplicationContext())
            .onResult(new Async.ResultHandler<ChatUIClient>() {
              @Override
              public void handleResult(Async<?> operation,
                                       @NonNull ChatUIClient chatUIClient) {
                chatUIClient.addSessionStateListener(new SnapinsChatSessionListener(mActivity));
                chatUIClient.startChatSession(mActivity);
              }
            });
  }

  /**
   * Starts the SOS UI.
   */
  public void startSOS() {
    assert (mActivity != null);

    if (ORG_ID == INCOMPLETE_ORG_SETTING ||
            POD_NAME == INCOMPLETE_ORG_SETTING ||
            SOS_DEPLOYMENT_ID == INCOMPLETE_ORG_SETTING) {
      notYetConfiguredMessage("SOS");
      return;
    }

    // First lets get rid of the Knowledge UI
    stopKnowledge();

    // Create an SOS options object
    SosOptions options = new SosOptions(POD_NAME, ORG_ID, SOS_DEPLOYMENT_ID);

    // Start an SOS session
    Sos.session(options).start(mActivity);
  }
}
